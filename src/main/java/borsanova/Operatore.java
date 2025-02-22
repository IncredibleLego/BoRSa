package borsanova;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * La classe Operatore rappresenta un'{@link Operatore} di borsa che può acquistare e vendere azioni in una o più {@link Borsa}.
 * <p>
 * Un operatore è caratterizzato da un nome (non vuoto) e da un budget.
 * <p>
 * Ogni operatore può decidere di acquistare o vendere azioni di una o più aziende presenti in una o più borse.
 * <p>
 * Operatore implementa dei metodi per depositare e prelevare denaro dal proprio budget, per acquistare e vendere azioni e per calcolare il capitale totale dell'operatore.
 * <p>
 * 
 * Rappresentazione dello stato:
 * - nome: il nome dell'operatore, non può essere nullo o vuoto.
 * - budget: il budget dell'operatore, non può essere negativo.
 * - borseConAzioni: una lista delle borse in cui l'operatore possiede azioni.
 * 
 */
public class Operatore implements Comparable<Operatore>{

    /*
     * Funzione di astrazione:
     * - AF(nome, budget, borseConAzioni) = un operatore con il nome nome, un budget budget e azioni nelle borse borseConAzioni
     * - Un'istanza di Operatore rappresenta un operatore di borsa identificato da un nome ed un budget e che possiede azioni in una o più borse
     */

    /*
     * Invariante di rappresentazione:
     * - Il nome dell'operatore deve essere non nullo 
     * - Il nome dell'operatore deve essere non vuoto
     * - Il budget dell'operatore deve essere non negativo
     * - borseConAzioni non contiene duplicati
     * - per ogni borsa b in borseConAzioni, b != null
     * - per ogni borsa b in borseConAzioni, l'operatore possiede azioni in b
     * - per ogni borsa b in borseConAzioni, il valore delle azioni possedute dall'operatore in b è maggiore o uguale a zero
     */

    /**
     * Insieme dei nomi degli {@link Operatore} già utilizzati.
     */
    private static final SortedSet<String> NOMI_USATI = new TreeSet<>();

    /**
     * Il nome dell'{@link Operatore}.
     */
    private final String nome;

    /**
     * Il budget dell'{@link Operatore}.
     */
    private int budget;

    /**
     * La lista delle {@link Borsa} in cui l'{@link Operatore} possiede azioni.
     */
    private final List<Borsa> borseConAzioni = new ArrayList<>();

    /**
     * Crea un'istanza di {@link Operatore} con il nome specificato. Questo metodo assicura che il nome 
     * dell'operatore sia valido e unico utilizzando l'insieme {@link #NOMI_USATI}.
     * <p>
     * 
     * MODIFIES: {@link #NOMI_USATI}
     * EFFECTS: crea un nuovo operatore con il nome specificato e aggiunge il nome a NOMI_USATI
     * 
     * @param nome il nome dell'operatore da creare; non può essere {@code null} o vuoto.
     * @return una nuova istanza di {@link Operatore} con il nome specificato.
     * @throws NullPointerException se il nome è {@code null}.
     * @throws IllegalArgumentException se il nome è vuoto.
     * @throws IllegalArgumentException se il nome è già utilizzato.
     */
    public static Operatore of(final String nome) {
        if (Objects.requireNonNull(nome, "Il nome dell'operatore non può essere nullo").isBlank()) {
            throw new IllegalArgumentException("Il nome dell'operatore non può essere vuoto");
        }
        if (NOMI_USATI.contains(nome)) {
            throw new IllegalArgumentException("Il nome dell'operatore è già stato utilizzato");
        }
        NOMI_USATI.add(nome);
        return new Operatore(nome);
    }

    /**
     * Costruttore privato per creare un'istanza di {@code Operatore}.
     * Questo costruttore è utilizzato internamente dalla classe per garantire
     * che le istanze siano create solo tramite il metodo {@link #of(String)}.
     * <p>
     * 
     * EFFECTS: crea un nuovo operatore con il nome specificato
     * 
     * @param nome il nome dell'{@link Operatore}; deve essere valido e non vuoto.
     * @throws IllegalArgumentException se il nome è vuoto.
     */
    private Operatore(final String nome) {
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Il nome dell'Operatore non può essere vuoto");
        }
        this.nome = nome;
        this.budget = 0;
    }

    /**
     * Restituisce il nome dell'{@link Operatore}.
     * <p>
     * 
     * EFFECTS: restituisce il nome dell'operatore
     * 
     * @return il nome dell'{@link Operatore}
     */
    public String getNome() {
        return nome;
    }

    /**
     * Restituisce il budget dell'{@link Operatore}.
     * <p>
     * 
     * EFFECTS: restituisce il budget dell'operatore
     * 
     * @return il budget dell'{@link Operatore}
     */
    public int getBudget() {
        return budget;
    }

    /**
     * Restituisce la lista delle {@link Borsa} in cui l'{@link Operatore} possiede azioni.
     * <p>
     * 
     * EFFECTS: restituisce la lista delle borse in cui l'operatore possiede azioni
     * 
     * @return la lista delle {@link Borsa} in cui l'{@link Operatore} possiede azioni
     */
    public List<Borsa> getBorseConAzioni() {
        return borseConAzioni;
    }

    /**
     * Restituisce il capitale totale dell'{@link Operatore}, ovvero la somma del valore delle azioni possedute in tutte le borse.
     * <p>
     * Sommando questo valore al budget si ottiene la somma di denaro totale dell'operatore.
     * <p>
     * 
     * EFFECTS: restituisce il capitale totale dell'operatore
     * 
     * @return il capitale totale dell'{@link Operatore}
     */
    public int getCapitaleTotale(){
        int capitale = 0;
        for (Borsa borsa : borseConAzioni) {
            try {
                capitale += borsa.valoreAzioni(this);
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }
        }
        return capitale;
    }

    /**
     * Deposita una somma di denaro nel budget dell'{@link Operatore}.
     * <p>
     * 
     * MODIFIES: this.budget
     * EFFECTS: deposita la somma specificata nel budget dell'operatore
     * 
     * @param somma la somma di denaro da depositare; deve essere maggiore di zero.
     * @throws IllegalArgumentException se la somma è minore o uguale a zero.
     */
    public void deposita(int somma){
        if (somma <= 0){
            throw new IllegalArgumentException("La somma da depositare deve essere maggiore di zero");
        }
        this.budget += somma;
    }

    /**
     * Preleva una somma di denaro dal budget dell'{@link Operatore}.
     * <p>
     * 
     * MODIFIES: this.budget
     * EFFECTS: preleva la somma specificata dal budget dell'operatore
     * 
     * @param somma la somma di denaro da prelevare; deve essere maggiore di zero e minore o uguale al budget.
     * @throws IllegalArgumentException se la somma è minore o uguale a zero.
     * @throws IllegalArgumentException se la somma è maggiore del budget.
     */
    public void preleva(int somma){
        if (somma <= 0){
            throw new IllegalArgumentException("La somma da prelevare deve essere maggiore di zero");
        }
        if (somma > this.budget){
            throw new IllegalArgumentException("La somma da prelevare è maggiore del budget");
        }
        this.budget -= somma;
    }

    /**
     * Acquista un numero specificato di azioni di una determinata {@link Azienda} in una {@link Borsa} specificata.
     * <p>
     * 
     * MODIFIES: this, borsa
     * EFFECTS: acquista il numero specificato di azioni dell'azienda nella borsa specificata
     * 
     * @param borsa la {@link Borsa} in cui acquistare le azioni; non può essere {@code null}.
     * @param azienda l'{@link Azienda} di cui acquistare le azioni; non può essere {@code null}.
     * @param nAzioni il numero di azioni da acquistare; deve essere maggiore di zero.
     * @throws NullPointerException se la borsa è {@code null}.
     * @throws NullPointerException se l'azienda è {@code null}.
     * @throws IllegalArgumentException se il numero di azioni è minore o uguale a zero.
     * @throws IllegalArgumentException se il budget non è sufficiente per acquistare le azioni.
     */
    public void acquista(Borsa borsa, Azienda azienda, int nAzioni) {
        Objects.requireNonNull(borsa, "La borsa non può essere nulla");
        Objects.requireNonNull(azienda, "L'azienda non può essere nulla");
        if (nAzioni <= 0) {
            throw new IllegalArgumentException("Il numero di azioni deve essere maggiore di zero");
        }
        int prezzoAzione = borsa.getPrezzoAzione(azienda);
        int investimento = prezzoAzione * nAzioni;
        if (investimento > this.budget) {
            throw new IllegalArgumentException("Il budget non è sufficiente per acquistare le azioni");
        }
        borsa.acquistaAzioni(this, azienda, nAzioni);
        this.budget -= investimento;
        if (!borseConAzioni.contains(borsa)) {
            borseConAzioni.add(borsa);
        }
    }

    /**
     * Vende un numero specificato di azioni di una determinata {@link Azienda} in una {@link Borsa} specificata.
     * <p>
     * 
     * MODIFIES: this, borsa
     * EFFECTS: vende il numero specificato di azioni dell'azienda nella borsa specificata
     * 
     * @param borsa la {@link Borsa} in cui vendere le azioni; non può essere {@code null}.
     * @param azienda l'{@link Azienda} di cui vendere le azioni; non può essere {@code null}.
     * @param nAzioni il numero di azioni da vendere; deve essere maggiore di zero.
     * @throws NullPointerException se la borsa è {@code null}.
     * @throws NullPointerException se l'azienza è {@code null}.
     * @throws IllegalArgumentException se il numero di azioni è minore o uguale a zero.
     */
    public void vendi(Borsa borsa, Azienda azienda, int nAzioni) {
        Objects.requireNonNull(borsa, "La borsa non può essere nulla");
        Objects.requireNonNull(azienda, "L'azienda non può essere nulla");
        if (nAzioni <= 0) {
            throw new IllegalArgumentException("Il numero di azioni deve essere maggiore di zero");
        }
        int guadagno = borsa.getPrezzoAzione(azienda) * nAzioni;
        borsa.vendiAzioni(this, azienda, nAzioni);
        this.budget += guadagno;
    }

    /**
     * Confronta questo {@link Operatore} con l'{@link Operatore} specificato per ordine alfabetico del nome.
     * <p>
     * 
     * EFFECTS: confronta questo operatore con l'operatore specificato per ordine alfabetico del nome
     * 
     * @param Operatore l'{@link Operatore} da confrontare
     * @throws NullPointerException se l'{@link Operatore} è {@code null}
     * @throws ClassCastException se l'oggetto specificato non è un'istanza di {@link Operatore}
     * @return un valore negativo, zero o positivo se questo {@link Operatore} è rispettivamente minore, uguale o maggiore dell'{@link Operatore} specificato
     */
    @Override
    public int compareTo(Operatore Operatore) throws NullPointerException, ClassCastException{
        Objects.requireNonNull(Operatore, "L'operatore non può essere nulla");
        if (nome == null){
            throw new NullPointerException("Il nome dell'operatore non può essere nullo");
        }
        return nome.compareTo(Operatore.nome);
    }

    /**
     * Verifica se questo {@link Operatore} è uguale all'oggetto specificato.
     * 
     * @param o l'oggetto da confrontare con {@code this}
     * @return {@code true} se questo operatore è uguale all'oggetto specificato, false altrimenti
     * @throws NullPointerException se l'oggetto è {@code null}
     */
    @Override
    public boolean equals(Object o){
        Objects.requireNonNull(o, "L'oggetto non può essere nullo");
        if (this == o){
            return true;
        }
        if (!(o instanceof Operatore)){
            return false;
        }
        Operatore Operatore = (Operatore) o;
        return Objects.equals(nome, Operatore.nome);
    }

    /**
     * Restituisce il codice hash di questo operatore.
     * 
     * @return il codice hash di questo {@link Operatore}
     */
    @Override
    public int hashCode(){ 
        return Objects.hash(nome);
    }
}