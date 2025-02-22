/*

Copyright 2024 Massimo Santini

This file is part of "Programmazione 2 @ UniMI" teaching material.

This is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This material is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this file.  If not, see <https://www.gnu.org/licenses/>.

*/

package clients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import borsanova.Azienda;
import borsanova.Borsa;
import borsanova.Borsa.NessunaPolitica;
import borsanova.Operatore;

/** Client di test per alcune funzionalità relative alle <strong>borse</strong>. */
public class BorsaClient {

  /** . */
  private BorsaClient() {}

  /*-
   * Scriva un [@code main} che legge dal flusso in ingresso una sequenza di tre
   * gruppi di linee (separati tra loro dalla linea contenente solo --) ciascuno
   * della forma descritta di seguito:
   *
   *     nome_azienda nome_borsa numero prezzo_unitario
   *     ...
   *     --
   *     nome_operatore budget_iniziale
   *     ...
   *     --
   *     nome_operatore b nome_borsa nome_azienda prezzo_totale
   *     ... [oppure]
   *     nome_operatore s nome_borsa nome_azienda numero_azioni
   *
   * Assuma che i nomi non contengano spazi. In base al contenuto del primo
   * blocco, quota le azioni delle aziende nelle borse secondo il numero e
   * prezzo unitario specificati, in base al secondo blocco crea gli operatori
   * specificati con il budget iniziale specificato e in base al terzo blocco
   * esegue le operazioni, a seconda che il carattere che segue il nome
   * dell'operatore sia:
   *
   * - b compra azioni (quotate nella borsa e dell'azienda specificata,
   *   impegnano il prezzo totale specificato),
   * - s vende azioni (quotate nella borsa e dell'azienda specificata, nel
   *   numero specificato).
   *
   * Osservi che l'acquisto può determinare un resto, nel caso in cui il prezzo
   * totale non sia un multiplo esatto del prezzo dell'azione; tale resto rimane
   * a disposizione dell'operatore per eventuali operazioni successive.
   *
   * Al termine della lettura il programma emette nel flusso d'uscita (una per
   * linea) l'elenco delle borse coinvolte (in ordine alfabetico), per ogni
   * borsa emette l'elenco delle azioni in essa quotate (in ordine alfabetico,
   * prefissate da - e seguite dal numero di azioni ancora disponibili), e per
   * ognuna di esse i nomi degli operatori e delle quantità che ne possiedono
   * (in ordine alfabetico, prefissati da =).
   */
  @SuppressWarnings("Convert2Lambda") //Aggiunto per evitare warning VisualStudio su conversione a lambda
  public static void main(String[] args) {
    try (Scanner scanner = new Scanner(System.in)) {
      Map<String, Borsa> borse = new HashMap<>();
      Map<String, Operatore> operatori = new HashMap<>();
      Map<String, Azienda> aziende = new HashMap<>();
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        if (line.equals("--")) break;
        String[] parts = line.split(" ");
        String nomeAzienda = parts[0];
        String nomeBorsa = parts[1];
        int numero = Integer.parseInt(parts[2]);
        int prezzoUnitario = Integer.parseInt(parts[3]);
        Azienda azienda = aziende.get(nomeAzienda);
        if (azienda == null) {
          azienda = Azienda.of(nomeAzienda);
          aziende.put(nomeAzienda, azienda);
        }
        Borsa borsa = borse.computeIfAbsent(nomeBorsa, nome -> {
        Borsa nuovaBorsa = Borsa.of(nome);
        nuovaBorsa.setPoliticaDiPrezzo(new NessunaPolitica());
        return nuovaBorsa;
        });
        borsa.aggiungiQuotazione(azienda, prezzoUnitario, numero);
      }
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        if (line.equals("--")) break;
        String[] parts = line.split(" ");
        String nomeOperatore = parts[0];
        int budgetIniziale = Integer.parseInt(parts[1]);
        Operatore operatore = operatori.computeIfAbsent(nomeOperatore, Operatore::of);
        operatore.deposita(budgetIniziale);
      }
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) continue;
        String[] parts = line.split(" ");
        String nomeOperatore = parts[0];
        char operazione = parts[1].charAt(0);
        String nomeBorsa = parts[2];
        String nomeAzienda = parts[3];
        int quantita = Integer.parseInt(parts[4]);
        Operatore operatore = operatori.get(nomeOperatore);
        Borsa borsa = borse.get(nomeBorsa);
        Azienda azienda = aziende.get(nomeAzienda);
        if (operazione == 'b') {
          int prezzoAzione = borsa.getPrezzoAzione(azienda);
          int numeroAzioni = quantita / prezzoAzione;
          operatore.acquista(borsa, azienda, numeroAzioni);
        } else if (operazione == 's') {
          operatore.vendi(borsa, azienda, quantita);
        }
      }
      List<Borsa> listaBorse = new ArrayList<>(borse.values());
      Collections.sort(listaBorse);
      for (Borsa borsa : listaBorse) {
        System.out.println(borsa.getNome());
        List<Borsa.Azione> listaAzioni = new ArrayList<>(borsa.getAzioni());
        Collections.sort(listaAzioni, new Comparator<Borsa.Azione>() {
          @Override
          public int compare(Borsa.Azione a1, Borsa.Azione a2) {
            return a1.getAzienda().getNome().compareTo(a2.getAzienda().getNome());
          }
        });
        for (var azione : listaAzioni) {
          System.out.println("- " + azione.getAzienda().getNome() + " " + azione.getNDisponibili());
          List<Operatore> listaOperatori = new ArrayList<>(borsa.getOperatori().keySet());
          Collections.sort(listaOperatori);
          for (Operatore operatore : listaOperatori) {
            try {
              int nAzioni = borsa.getAzioniPossedute(operatore, azione.getAzienda());
              System.out.println("= " + operatore.getNome() + " " + nAzioni);
            } catch (IllegalArgumentException e) {
              System.err.println(e.getMessage());
            }
          }
        }
      }
    } catch (NumberFormatException e) {
      System.err.println("Errore: " + e.getMessage());
    }
  }
}