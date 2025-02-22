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
import borsanova.Operatore;

/** Client di test per alcune funzionalità relative agli <strong>operatori</strong>. */
public class OperatoreClient {

  /** . */
  private OperatoreClient() {}

  /*-
   * Scriva un {@code main} che legge dal flusso di ingresso una sequenza di tre
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
   *     ... [oppure]
   *     nome_operatore d valore
   *     ... [oppure] nome_operatore w valore
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
   * - d deposita denaro (secondo il valore specificato),
   * - w preleva denaro (secondo il valore specificato).
   *
   * Osservi che l'acquisto può determinare un resto, nel caso in cui il prezzo
   * totale non sia un multiplo esatto del prezzo dell'azione; tale resto rimane
   * a disposizione dell'operatore per eventuali operazioni successive.
   *
   * Al termine della lettura il programma emette nel flusso d'uscita l'elenco
   * degli operatori coinvolti (in ordine alfabetico) ciascuno dei quali seguito
   * (sulla stessa linea e separato da virgole) dal suo budget finale e dalla
   * somma del valore delle azioni che possiede, ogni operatore è poi seguito
   * dall'elenco delle azioni che possiede, ciascuna azione va descritta
   * emettendo il nome della borsa (in ordine alfabetico, preceduto da -)
   * seguito da quello dell'azienda e dal numero di azioni possedute (separati
   * da virgole).
   */
  @SuppressWarnings("Convert2Lambda") //Inserito per evitare warning Visual Studio Code
  public static void main(String[] args) {
    try (Scanner scanner = new Scanner(System.in)) {
      Map<String, Borsa> borse = new HashMap<>();
      Map<String, Operatore> operatori = new HashMap<>();
      Map<String, Azienda> aziende = new HashMap<>();

      // Leggi il primo blocco
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        if (line.equals("--")) break;
        String[] parts = line.split(" ");
        String nomeAzienda = parts[0];
        String nomeBorsa = parts[1];
        int numero = Integer.parseInt(parts[2]);
        int prezzoUnitario = Integer.parseInt(parts[3]);

        Azienda azienda = aziende.computeIfAbsent(nomeAzienda, Azienda::of);
        Borsa borsa = borse.get(nomeBorsa);
        if (borsa == null) {
          borsa = Borsa.of(nomeBorsa);
          borse.put(nomeBorsa, borsa);
        }
        borsa.aggiungiQuotazione(azienda, prezzoUnitario, numero);
      }

      // Leggi il secondo blocco
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        if (line.equals("--")) break;
        String[] parts = line.split(" ");
        String nomeOperatore = parts[0];
        int budgetIniziale = Integer.parseInt(parts[1]);
        Operatore operatore = operatori.get(nomeOperatore);
        if (operatore == null) {
          operatore = Operatore.of(nomeOperatore);
          operatori.put(nomeOperatore, operatore);
        }
        operatore.deposita(budgetIniziale);
      }

      // Leggi il terzo blocco
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) continue;
        String[] parts = line.split(" ");
        String nomeOperatore = parts[0];
        char operazione = parts[1].charAt(0);
        Operatore operatore = operatori.get(nomeOperatore);
        switch (operazione) {
          case 'b' ->  {
            String nomeBorsa = parts[2];
            String nomeAzienda = parts[3];
            int prezzoTotale = Integer.parseInt(parts[4]);
            Borsa borsa = borse.get(nomeBorsa);
            Azienda azienda = aziende.get(nomeAzienda);
            int prezzoAzione = borsa.getPrezzoAzione(azienda);
            int numeroAzioni = (prezzoTotale / prezzoAzione);
            if (numeroAzioni > 0) {
              operatore.acquista(borsa, azienda, numeroAzioni);
            }
          }
          case 's' ->  {
            String nomeBorsa = parts[2];
            String nomeAzienda = parts[3];
            int quantita = Integer.parseInt(parts[4]);
            Borsa borsa = borse.get(nomeBorsa);
            Azienda azienda = aziende.get(nomeAzienda);
            operatore.vendi(borsa, azienda, quantita);
          }
          case 'd' ->  {
            int valore = Integer.parseInt(parts[2]);
            operatore.deposita(valore);
          }
          case 'w' ->  {
            int valore = Integer.parseInt(parts[2]);
            operatore.preleva(valore);
          }
          default -> {
              }
        }
        
      }
      

      // Stampa i risultati
      List<Operatore> listaOperatori = new ArrayList<>(operatori.values());
      Collections.sort(listaOperatori);

      for (Operatore operatore : listaOperatori) {
        System.out.println(operatore.getNome() + ", " + operatore.getBudget() + ", " + operatore.getCapitaleTotale());
        List<Borsa> listaBorse = new ArrayList<>(operatore.getBorseConAzioni());
        Collections.sort(listaBorse);

        for (Borsa borsa : listaBorse) {
          List<Borsa.Azione> listaAzioni = new ArrayList<>(borsa.getAzioni());
          Collections.sort(listaAzioni, new Comparator<Borsa.Azione>() {
            @Override
            public int compare(Borsa.Azione a1, Borsa.Azione a2) {
              return a1.getAzienda().getNome().compareTo(a2.getAzienda().getNome());
            }
          });

          for (Borsa.Azione azione : listaAzioni) {
            try {
              int nAzioni = borsa.getAzioniPossedute(operatore, azione.getAzienda());
              if (nAzioni > 0) {
                System.out.println("- " + borsa.getNome() + ", " + azione.getAzienda().getNome() + ", " + nAzioni);
              }
            } catch (IllegalArgumentException e) {
              System.err.println("Errore: " + e.getMessage());
            }
          }
        }
      }
    } catch (NumberFormatException e) {
      System.err.println("Errore di formato");
    }
  }
}