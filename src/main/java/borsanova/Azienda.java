package borsanova;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * La classe Azienda rappresenta un'{@link Azienda} quotata in una o più {@link Borsa}.
 * <p>
 * Ogni {@link Azienda} è identificata da un nome univoco e può essere quotata in una o più {@link Borsa}.
 * <p>
 * Gli oggetti di tipo {@link Azienda} sono immutabili.
 * <p>
 * Garantisce che un'{@link Azienda} sia creata solo con un nome valido e unico e non possa quotarsi più volte nella stessa {@link Borsa}.
 * <p>
 * La classe fornisce un metodo per quotare l'{@link Azienda} in una {@link Borsa} specificata con un numero di azioni e un prezzo iniziale.
 * <p>
 * 
 * 
 * Rappresentazione dello stato:
 * - nome: il nome dell'azienda, non può essere nullo o vuoto.
 * - borseQuotate: una lista delle borse in cui l'azienda è quotata.
 * 
 */
public class Azienda implements Comparable<Azienda>{

    /*
     * Funzione di astrazione:
     * - AF(nome, borseQuotate) = un'azienda con il nome nome quotata nelle borse borseQuotate
     * - Un'istanza di Azienda rappresenta un'azienda con il nome nome quotata nelle borse borseQuotate
     */

    /*
     * Invariante di rappresentazione:
     * - Il nome dell'azienda deve essere non nullo 
     * - Il nome dell'azienda deve essere non vuoto
     * - borseQuotate non contiene duplicati
     * - per ogni borsa b in borseQuotate, b != null
     * - per ogni borsa b in borseQuotate, b contiene una quotazione per questa azienda con un numero di azioni ed un prezzo maggiore di zero
     */

    /**
     * Insieme dei nomi delle {@link Azienda} già utilizzati.
     */
    private static final SortedSet<String> NOMI_USATI = new TreeSet<>();

    /**
     * Il nome dell'{@link Azienda}.
     */
    private final String nome;

    /**
     * La lista delle {@link Borsa} in cui l'{@link Azienda} è quotata.
     */
    private final List<Borsa> borseQuotate = new ArrayList<>();

    /**
     * Crea un'istanza di {@link Azienda} con il nome specificato. Questo metodo assicura che il nome 
     * dell'azienda sia valido e unico utilizzando l'insieme {@link #NOMI_USATI}.
     * <p>
     * 
     * MODIFIES: {@link #NOMI_USATI}
     * EFFECTS: crea una nuova azienda con il nome specificato
     * 
     * @param nome il nome dell'azienda da creare; non può essere {@code null} o vuoto.
     * @return una nuova istanza di {@link  Azienda} con il nome specificato.
     * @throws NullPointerException se il nome è {@code null}.
     * @throws IllegalArgumentException se il nome è vuoto.
     * @throws IllegalArgumentException se il nome è già utilizzato.
     */
    public static Azienda of(final String nome) {
        if (Objects.requireNonNull(nome, "Il nome dell'azienda non può essere nullo").isBlank()) {
            throw new IllegalArgumentException("Il nome dell'azienda non può essere vuoto");
        }
        if (NOMI_USATI.contains(nome)) {
            throw new IllegalArgumentException("Il nome dell'azienda è già stato utilizzato");
        }
        NOMI_USATI.add(nome);
        return new Azienda(nome);
    }

    /**
     * Costruttore privato per creare un'istanza di {@code Azienda}.
     * Questo costruttore è utilizzato internamente dalla classe per garantire
     * che le istanze siano create solo tramite il metodo {@link #of(String)}.
     * <p>
     * 
     * REQUIRES: nome non nullo e non vuoto
     * EFFECTS: crea una nuova azienda con il nome specificato
     * 
     * @param nome il nome dell'{@link Azienda}; deve essere valido e non vuoto.
     * @throws IllegalArgumentException se il nome è vuoto.
     */
    private Azienda(final String nome) {
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Il nome dell'azienda non può essere vuoto");
        }
        this.nome = nome;
    }

    /**
     * Restituisce il nome dell'{@link Azienda}.
     * <p>
     * 
     * EFFECTS: restituisce il nome dell'azienda
     * 
     * @return il nome dell'{@link Azienda}
     */
    public String getNome() {
        return nome;
    }

    /**
     * Restituisce la lista delle {@link Borsa} in cui l'{@link Azienda} è quotata.
     * <p>
     * 
     * EFFECTS: restituisce la lista delle borse in cui l'azienda è quotata
     * 
     * @return la lista delle {@link Borsa} in cui l'{@link Azienda} è quotata
     */
    public List<Borsa> getBorseQuotate() {
        return borseQuotate;
    }

    /**
     * Quota l'{@link Azienda} nella {@link Borsa} specificata con il numero di azioni e il prezzo iniziale specificati.
     * Dato il controllo effettuato utilizzando {@link #NOMI_USATI} possiamo affermare che l'azienda non è già quotata nella borsa specificata.
     * <p>
     * 
     * MODIFIES: this (borseQuotate)
     * EFFECTS: quota l'azienda nella borsa specificata
     * 
     * @param borsa la {@link Borsa} in cui quotare l'{@link Azienda}
     * @param nAzioni il numero di azioni da quotare
     * @param prezzoIniziale il prezzo iniziale delle azioni
     * @throws NullPointerException se la {@link Borsa} è {@code null}
     * @throws IllegalArgumentException se il numero di azioni è minore o uguale a zero
     * @throws IllegalArgumentException se il prezzo iniziale dell'azione è minore o uguale a zero
     * @throws IllegalArgumentException se la {@link Azienda} è già quotata nella {@link Borsa}
     */
    public void quotaAzienda(Borsa borsa, int nAzioni, int prezzoIniziale){
        Objects.requireNonNull(borsa, "La borsa non può essere nulla");
        if (nAzioni <= 0){
            throw new IllegalArgumentException("Il numero di azioni deve essere maggiore di zero");
        }
        if (prezzoIniziale <= 0){
            throw new IllegalArgumentException("Il prezzo iniziale deve essere maggiore di zero");
        }
        if (borseQuotate.contains(borsa)){
            throw new IllegalArgumentException("L'azienda è già quotata in questa borsa");
        }
        borsa.aggiungiQuotazione(this, nAzioni, prezzoIniziale);
        borseQuotate.add(borsa);
    }
    
    /**
     * Confronta questa {@link Azienda} con l'{@link Azienda} specificata per ordine alfabetico del nome.
     * <p>
     * 
     * EFFECTS: confronta questa azienda con l'azienda specificata per ordine alfabetico del nome
     * 
     * @param azienda l'{@link Azienda} da confrontare
     * @throws NullPointerException se l'{@link Azienda} è {@code null}
     * @throws ClassCastException se l'oggetto specificato non è un'istanza di {@link Azienda}
     * @return un valore negativo, zero o positivo se questa {@link Azienda} è rispettivamente minore, uguale o maggiore dell'{@link Azienda} specificata
     */
    @Override
    public int compareTo(Azienda azienda) throws NullPointerException, ClassCastException{
        Objects.requireNonNull(azienda, "L'azienda non può essere nulla");
        if (nome == null){
            throw new NullPointerException("Il nome dell'azienda non può essere nullo");
        }
        return nome.compareTo(azienda.nome);
    }

    /**
     * Verifica se questa {@link Azienda} è uguale all'oggetto specificato.
     * 
     * @param o l'oggetto da confrontare con {@code this}
     * @return {@code true} se questa azienda è uguale all'oggetto specificato, false altrimenti
     * @throws NullPointerException se l'oggetto è {@code null}
     */
    @Override
    public boolean equals(Object o){
        Objects.requireNonNull(o, "L'oggetto non può essere nullo");
        if (this == o){
            return true;
        }
        if (!(o instanceof Azienda)){
            return false;
        }
        Azienda azienda = (Azienda) o;
        return Objects.equals(nome, azienda.nome);
    }

    /**
     * Restituisce il codice hash di questa azienda.
     * 
     * @return il codice hash di questa {@link Azienda}
     */
    @Override
    public int hashCode(){ 
        return Objects.hash(nome);
    }
}