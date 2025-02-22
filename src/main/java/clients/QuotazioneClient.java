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
import java.util.Set;
import java.util.TreeSet;

import borsanova.Azienda;
import borsanova.Borsa;

/** Client di test per alcune funzionalità relative alle <strong>quotazioni</strong>. */
public class QuotazioneClient {

  /** . */
  private QuotazioneClient() {}

  /*-
   * Scriva un {@code main} che legge dal flusso di ingresso una sequenza di
   * linee, ciascuna delle quali corrispondente ad una quotazione di un'azienda
   * in una borsa, della forma:
   *
   *     nome_azienda nome_borsa numero prezzo_unitario
   *
   * Assuma che i nomi non contengano spazi. In base al contenuto del flusso,
   * crei le aziende e le borse e le colleghi secondo le quotazioni specificate.
   * Al termine della lettura il programma emette nel flusso d'uscita l'elenco
   * delle aziende coinvolte (in ordine alfabetico) ciascuna delle quali seguita
   * (sulla stessa linea e separata da virgole) dall'elenco delle borse in cui
   * è quotata (in ordine alfabetico, precedute da -). Successivamente emette
   * l'elenco delle borse coinvolte (in ordine alfabetico) ciascuna delle quali
   * seguita (sulla stessa linea e separata da virgole) dall'elenco delle
   * aziende quotate (in ordine alfabetico, precedute da -).
   */
  public static void main(String[] args) {
    try (Scanner scanner = new Scanner(System.in)) {
      Map<String, Azienda> aziende = new HashMap<>();
      Map<String, Borsa> borse = new HashMap<>();
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) continue;
        String[] parts = line.split(" ");
        String nomeAzienda = parts[0];
        String nomeBorsa = parts[1];
        int numero = Integer.parseInt(parts[2]);
        int prezzoUnitario = Integer.parseInt(parts[3]);

        Azienda azienda;
        if (aziende.containsKey(nomeAzienda)) {
          azienda = aziende.get(nomeAzienda);
        } else {
          azienda = Azienda.of(nomeAzienda);
          aziende.put(nomeAzienda, azienda);
        }

        Borsa borsa;
        if (borse.containsKey(nomeBorsa)) {
          borsa = borse.get(nomeBorsa);
        } else {
          borsa = Borsa.of(nomeBorsa);
          borse.put(nomeBorsa, borsa);
        }
        borsa.aggiungiQuotazione(azienda, prezzoUnitario, numero);
        azienda.quotaAzienda(borsa, numero, prezzoUnitario);
      }
      List<Azienda> listaAziende = new ArrayList<>(aziende.values());
      Collections.sort(listaAziende);
      for (Azienda azienda : listaAziende) {
        System.out.print(azienda.getNome());
        Set<Borsa> borseUniche = new TreeSet<>(azienda.getBorseQuotate());
        for (Borsa borsa : borseUniche) {
          System.out.print("\n- " + borsa.getNome());
        }
        System.out.println();
      }
      List<Borsa> listaBorse = new ArrayList<>(borse.values());
      Collections.sort(listaBorse);
      for (Borsa borsa : listaBorse) {
        System.out.print(borsa.getNome());
        @SuppressWarnings("Convert2Lambda") //Aggiunto per evitare warning visualStudio su conversione a lambda
        Set<Borsa.Azione> azioniUniche = new TreeSet<>(new Comparator<Borsa.Azione>() {
          @Override
          public int compare(Borsa.Azione a1, Borsa.Azione a2) {
            return a1.getAzienda().getNome().compareTo(a2.getAzienda().getNome());
          }
        });
        azioniUniche.addAll(borsa.getAzioni());
        for (Borsa.Azione azione : azioniUniche) {
          System.out.print("\n- " + azione.getAzienda().getNome());
        }
        System.out.println();
      }
    } catch (NumberFormatException e) {
      System.err.println("Errore: " + e.getMessage());
    }
  }
}