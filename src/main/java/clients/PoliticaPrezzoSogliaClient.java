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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import borsanova.Azienda;
import borsanova.Borsa;
import borsanova.Operatore;

/** Client di test per alcune funzionalità relative alle <strong>borse</strong>. */
public class PoliticaPrezzoSogliaClient {

  /** . */
  private PoliticaPrezzoSogliaClient() {}

  /*-
   * Scriva un {@code main} che riceve come parametri sulla linea di comando
   *
   *      nome_borsa soglia nome_operatore budget_iniziale
   *
   * il secondo parametro è un intero che determina la politica di prezzo della
   * borsa come segue: se l'acquisto coinvolge un numero di azioni maggiore
   * della soglia, il prezzo dell'azione raddoppia (altrimenti resta identico),
   * se la vendita coinvolge un numero di azioni maggiore della soglia, il
   * prezzo dell'azione viene diviso per due (ma senza scendere mai sotto 1,
   * resta invariato nel caso in cui la soglia non sia superata).
   *
   * Il programma quindi procede esattamente come nel caso della classe
   * PoliticaPrezzoClient, ossia: legge dal flusso in ingresso una sequenza di
   * due gruppi di linee (separati tra loro dalla linea contenente solo --)
   * ciascuno della forma descritta di seguito:
   *
   *     nome_azienda numero prezzo_unitario
   *     ...
   *     --
   *     b nome_azienda prezzo_totale
   *     ... [oppure]
   *     s nome_azienda numero_azioni
   *
   * in base al contenuto del primo blocco, quota le azioni delle aziende
   * specificate nella borsa (definita dal primo parametro sulla linea di
   * comando) secondo il numero e prezzo unitario specificati, in base al
   * contenuto del secondo blocco — una volta creato un operatore (di nome e
   * budget iniziale specificati dal terzo e quarto parametro sulla linea di
   * comando) — esegue le operazioni a seconda che il carattere che segue il
   * nome dell'operatore sia:
   *
   * - b compra azioni (dell'azienda specificata, impegnano il prezzo totale
   *   specificato),
   * - s vende azioni (dell'azienda specificata, nel numero specificato).
   *
   * Al termine della lettura il programma emette nel flusso d'uscita l'elenco
   * delle azioni (in ordine alfabetico) seguite dal prezzo (separato da una
   * virgola).
   */
  @SuppressWarnings("Convert2Lambda") //Inserito per evitare warning Visual Studio Code su espresse lambda
  public static void main(String[] args) throws Exception {
    if (args.length < 4) {
      System.err.println("Utilizzo: java PoliticaPrezzoSogliaClient nome_borsa soglia nome_operatore budget_iniziale");
      return;
    }

    String nomeBorsa = args[0];
    int soglia = Integer.parseInt(args[1]);
    String nomeOperatore = args[2];
    int budgetIniziale = Integer.parseInt(args[3]);

    Borsa borsa = Borsa.of(nomeBorsa);
    borsa.setPoliticaDiPrezzo(borsa.new VariazioneSoglia(soglia));

    Operatore operatore = Operatore.of(nomeOperatore);
    operatore.deposita(budgetIniziale);

    Map<String, Azienda> aziende = new HashMap<>();

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    String line;

    // Leggi il primo blocco
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (line.equals("--")) break;
      String[] parts = line.split(" ");
      String nomeAzienda = parts[0];
      int numero = Integer.parseInt(parts[1]);
      int prezzoUnitario = Integer.parseInt(parts[2]);

      Azienda azienda = aziende.get(nomeAzienda);
      if (azienda == null) {
        azienda = Azienda.of(nomeAzienda);
        aziende.put(nomeAzienda, azienda);
      }
      borsa.aggiungiQuotazione(azienda, prezzoUnitario, numero);
    }

    // Leggi il secondo blocco
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (line.isEmpty()) continue;
      String[] parts = line.split(" ");
      char operazione = parts[0].charAt(0);
      String nomeAzienda = parts[1];

      Azienda azienda = aziende.get(nomeAzienda);
      if (azienda == null) {
        System.err.println("Errore: azienda non trovata");
        continue;
      }

      switch (operazione) {
        case 'b' ->  {
          int prezzoTotale = Integer.parseInt(parts[2]);
          int prezzoAzione = borsa.getPrezzoAzione(azienda);
          int numeroAzioni = prezzoTotale / prezzoAzione;
          if (numeroAzioni > 0) {
            operatore.acquista(borsa, azienda, numeroAzioni);
          }
        }
        case 's' ->  {
          int numeroAzioni = Integer.parseInt(parts[2]);
          operatore.vendi(borsa, azienda, numeroAzioni);
        }
        default -> System.err.println("Errore: operazione non riconosciuta");
      }
    }

    // Stampa i risultati
    List<Borsa.Azione> listaAzioni = new ArrayList<>(borsa.getAzioni());
    Collections.sort(listaAzioni, new Comparator<Borsa.Azione>() {
      @Override
      public int compare(Borsa.Azione a1, Borsa.Azione a2) {
        return a1.getAzienda().getNome().compareTo(a2.getAzienda().getNome());
      }
    });

    for (Borsa.Azione azione : listaAzioni) {
      System.out.println(azione.getAzienda().getNome() + ", " + azione.getPrezzo());
    }
  }
}