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

import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import borsanova.Borsa;

/** Client di test per alcune funzionalità relative alle <strong>aziende</strong>. */
public class OmonimiaBorsaClient {

  /** . */
  private OmonimiaBorsaClient() {}

  /*-
   * Scriva un {@code main} che legge dal flusso di ingresso una sequenza di
   * linee, ciascuna delle quali corrispondente ad un nome di borsa ed emette
   * nel flusso d'uscita l'elenco di tali nomi di borsa in ordine alfabetico e
   * senza ripetizioni.
   */
    public static void main(String[] args) {
    SortedSet<Borsa> borse = new TreeSet<>();
    try (Scanner scanner = new Scanner(System.in)) {
        while (scanner.hasNextLine()) {
            String nome = scanner.nextLine().trim();
            if (!nome.isEmpty()) {
                try {
                    Borsa borsa = Borsa.of(nome);
                    borse.add(borsa);
                } catch (IllegalArgumentException e) {
                    System.err.println("Errore: " + e.getMessage());
                }
            }
        }
    }
    for (Borsa Borsa : borse) {
        System.out.println(Borsa.getNome());
    }
  }
}