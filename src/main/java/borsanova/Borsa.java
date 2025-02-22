package borsanova;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

//Codice di Condotta: Per tutta la realizzazione del progetto, mi sono avvalso dei consigli e suggerimenti di altri colleghi studenti e di intelligenza artificiale come Copilot e ChatGPT
//Codice di Condotta: per avere una visione più ampia e completa del problema e delle possibili soluzioni. Il codice implementato e le scelte implementative tuttavia sono frutto del mio lavoro e della mia comprensione del problema.

/**
 * La classe Borsa rappresenta una borsa valori in cui sono quotate azioni di diverse aziende.
 * <p>
 * Ogni {@link Borsa} è identificata da un nome univoco e contiene una lista di azioni quotate e un insieme di operatori che possiedono azioni.
 * <p>
 * Gli oggetti di tipo {@link Borsa} sono immutabili.
 * <p>
 * Garantisce che una {@link Borsa} sia creata solo con un nome valido e unico e possa gestire le quotazioni delle azioni e le operazioni di acquisto e vendita.
 * <p>
 * La classe fornisce metodi per aggiungere quotazioni, acquistare e vendere azioni, e ottenere informazioni sulle azioni e sugli operatori.
 * <p>
 * La classe ha una Inner Class {@link Azione} che rappresenta un'azione quotata in una borsa.
 * <p>
 * La classe ha una Inner Class {@link Allocazione} che rappresenta l'allocazione di azioni di un operatore in una borsa.
 * <p> 
 * É inoltre possibile impostare una politica di prezzo per le azioni quotate nella borsa tramite l'interfaccia {@link PoliticaDiPrezzo}.
 * 
 * 
 * Rappresentazione dello stato:
 * - nome: il nome della borsa, non può essere nullo o vuoto.
 * - azioni: una lista delle azioni quotate nella borsa.
 * - operatori: una mappa degli operatori e delle loro allocazioni di azioni.
 * - politicaDiPrezzo: la politica di prezzo applicata alle azioni nella borsa.
 * 
 */
public class Borsa implements Comparable<Borsa>{

    /*
     * Funzione di astrazione:
     * - AF(nome, azioni, operatori, politicaDiPrezzo) = una borsa con il nome nome, azioni quotate azioni, operatori operatori e politica di prezzo politicaDiPrezzo
     * - Un'istanza di Borsa rappresenta una borsa dove le aziende possono quotarsi e gli operatori possono acquistare e vendere azioni, modificate da una politica di prezzo
     */

    /*
     * Invariante di rappresentazione:
     * - Il nome della borsa deve essere non nullo 
     * - Il nome della borsa deve essere non vuoto
     * - azioni non contiene duplicati
     * - per ogni azione a in azioni, a != null
     * - operatori non contiene duplicati
     * - per ogni operatore o in operatori, o != null
     * - politicaDiPrezzo non può essere nulla
     * - per ogni operatore o in operatori, o possiede azioni in azioni
     * - per ogni azione a in azioni, a è quotata nella borsa
     * - per ogni azione a in azioni, a ha un prezzo valido
     * - per ogni azione a in azioni, a ha un numero di azioni disponibili valido
     * - per ogni azione a in azioni, a ha un numero totale di azioni valido
     * - per ogni operatore o in operatori, o possiede azioni di a in azioni
     * - per ogni operatore o in operatori, o ha un valore totale delle azioni valido
     * - per ogni azione a in azioni, a ha un prezzo valido
     * - un operatore non può vendere più azioni di quante ne possiede
     * - un operatore non può acquistare più azioni di quante ne sono disponibili
     */

    /**
     * Insieme dei nomi delle {@link Borsa} già utilizzati.
     */
    private static final SortedSet<String> NOMI_USATI = new TreeSet<>();

    /**
     * Il nome della {@link Borsa}.
     */
    private final String nome;

    /**
     * La lista delle azioni quotate nella {@link Borsa}.
     */
    private final List<Azione> azioni = new ArrayList<>();

    /**
     * La mappa degli operatori e delle loro allocazioni di azioni nella {@link Borsa}.
     */
    private final Map<Operatore, Allocazione> operatori = new HashMap<>();

    /**
     * La politica di prezzo applicata alle azioni nella {@link Borsa}.
     */
    private PoliticaDiPrezzo politicaDiPrezzo;

    /**
     * Crea un'istanza di {@link Borsa} con il nome specificato. Questo metodo assicura che il nome 
     * della borsa sia valido e unico utilizzando l'insieme {@link #NOMI_USATI}.
     * <p>
     * 
     * MODIFIES: NOMI_USATI
     * EFFECTS: crea una nuova borsa con il nome specificato e aggiunge il nome a NOMI_USATI
     * 
     * @param nome il nome della {@link Borsa} da creare; non può essere {@code null} o vuoto.
     * @return una nuova istanza di {@link Borsa} con il nome specificato.
     * @throws NullPointerException se il nome è {@code null}.
     * @throws IllegalArgumentException se il nome è vuoto.
     * @throws IllegalArgumentException se il nome è già utilizzato.
     */
    public static Borsa of(final String nome) {
        if (Objects.requireNonNull(nome, "Il nome della borsa non può essere nullo").isBlank()) {
            throw new IllegalArgumentException("Il nome della borsa non può essere vuoto");
        }
        if (NOMI_USATI.contains(nome)) {
            throw new IllegalArgumentException("Il nome della borsa è già stato utilizzato");
        }
        NOMI_USATI.add(nome);
        return new Borsa(nome);
    }

    /**
     * Costruttore privato per creare un'istanza di {@code Borsa}.
     * Questo costruttore è utilizzato internamente dalla classe per garantire
     * che le istanze siano create solo tramite il metodo {@link #of(String)}.
     * <p>
     * 
     * EFFECTS: crea una nuova borsa con il nome specificato
     * 
     * @param nome il nome della {@link Borsa}; deve essere valido e non vuoto.
     * @throws IllegalArgumentException se il nome è vuoto.
     */
    private Borsa(final String nome) {
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Il nome della borsa non può essere vuoto");
        }
        this.nome = nome;
    }

    /**
     * Restituisce il nome della {@link Borsa}.
     * <p>
     * 
     * EFFECTS: restituisce il nome della borsa
     * 
     * @return il nome della {@link Borsa}
     */
    public String getNome() {
        return nome;
    }

    /**
     * Restituisce la lista delle azioni quotate nella {@link Borsa}.
     * <p>
     * 
     * EFFECTS: restituisce la lista delle azioni quotate nella borsa
     * 
     * @return la lista delle {@link Azione} quotate nella {@link Borsa}
     */
    public List<Azione> getAzioni(){
        return azioni;
    }

    /**
     * Restituisce la mappa degli {@link Operatore} e delle loro {@link Allocazione} di {@link Azione} nella {@link Borsa}.
     * <p>
     * 
     * EFFECTS: restituisce la mappa degli operatori e delle loro allocazioni di azioni nella borsa
     * 
     * @return la mappa degli {@link Operatore} e delle loro {@link Allocazione} di {@link Azione} nella {@link Borsa}
     */
    public Map<Operatore, Allocazione> getOperatori(){
        return operatori;
    }

    /**
     * Imposta la {@link PoliticaDiPrezzo} applicata alle azioni nella {@link Borsa}.
     * <p>
     * 
     * MODIFIES: this
     * EFFECTS: imposta la politica di prezzo applicata alle azioni nella borsa
     * 
     * @param politica la politica di prezzo da applicare; non può essere {@code null}.
     * @throws NullPointerException se la politica è {@code null}.
     */
    public void setPoliticaDiPrezzo(PoliticaDiPrezzo politica) {
        this.politicaDiPrezzo = Objects.requireNonNull(politica, "La politica di prezzo non può essere nulla");
    }

    /**
     * Restituisce la {@link PoliticaDiPrezzo} applicata alle azioni nella {@link Borsa}.
     * <p>
     * 
     * EFFECTS: restituisce la politica di prezzo applicata alle azioni nella borsa
     * 
     * @return la politica di prezzo applicata alle azioni nella {@link Borsa}
     */
    public PoliticaDiPrezzo getPoliticaDiPrezzo() {
        return politicaDiPrezzo;
    }

    /**
     * Restituisce il numero di {@link Azione} possedute da un {@link Operatore} per una determinata {@link Azienda} nella {@link Borsa}.
     * <p>
     * 
     * EFFECTS: restituisce il numero di azioni possedute dall'operatore per l'azienda nella borsa
     * 
     * @param operatore l'operatore di cui ottenere il numero di azioni; non può essere {@code null}.
     * @param azienda l'azienda di cui ottenere il numero di azioni; non può essere {@code null}.
     * @throws NullPointerException se l'operatore è {@code null}.
     * @throws NullPointerException se l'azienda è {@code null}.
     * @throws IllegalArgumentException se l'operatore non ha azioni in questa borsa.
     * @throws IllegalArgumentException se l'operatore non possiede azioni di questa azienda.
     * @return il numero di azioni possedute dall'operatore per l'azienda nella borsa
     */
    public Integer getAzioniPossedute(Operatore operatore, Azienda azienda){
        Objects.requireNonNull(operatore, "L'operatore non può essere nullo");
        Objects.requireNonNull(azienda, "L'azienda non può essere nulla");
        if (!operatori.containsKey(operatore)){
            throw new IllegalArgumentException("L'operatore non ha azioni in questa borsa");
        }
        Allocazione allocazione = operatori.get(operatore);
        Map<Azione, Integer> azion = allocazione.getAzioni();
        for (Iterator<Azione> it = azion.keySet().iterator(); it.hasNext(); ) {
            Azione azione = it.next();
            if (azione.getAzienda().equals(azienda)){
                return azion.get(azione);
            }
        }
        throw new IllegalArgumentException("L'operatore non possiede azioni di questa azienda");
    }
    
    /**
     * Restituisce il valore totale delle {@link Azione} possedute da un {@link Operatore} nella {@link Borsa}.
     * <p>
     * 
     * EFFECTS: restituisce il valore totale delle azioni possedute dall'operatore nella borsa
     * 
     * @param operatore l'operatore di cui ottenere il valore totale delle azioni; non può essere {@code null}.
     * @throws NullPointerException se l'operatore è {@code null}.
     * @throws IllegalArgumentException se l'operatore non ha azioni in questa borsa.
     * @return il valore totale delle azioni possedute dall'operatore nella borsa
     */
    public int valoreAzioni(Operatore operatore){
        Objects.requireNonNull(operatore, "L'operatore non può essere nullo");
        if (!operatori.containsKey(operatore)){
            throw new IllegalArgumentException("L'operatore non ha azioni in questa borsa");
        }
        Allocazione allocazione = operatori.get(operatore);
        Map<Azione, Integer> azion = allocazione.getAzioni();
        int valore = 0;
        for (Iterator<Azione> it = azion.keySet().iterator(); it.hasNext(); ) {
            Azione azione = it.next();
            int prezzo = azione.getPrezzo();
            int nAzioni = azion.get(azione);
            valore += prezzo * nAzioni;
        }
        return valore;
    }

    /**
     * Restituisce il prezzo di un'{@link Azione} di una determinata {@link Azienda} nella {@link Borsa}.
     * <p>
     * 
     * EFFECTS: restituisce il prezzo di un'azione dell'azienda nella borsa
     * 
     * @param azienda l'azienda di cui ottenere il prezzo dell'azione; non può essere {@code null}.
     * @throws NullPointerException se l'azienda è {@code null}.
     * @throws IllegalArgumentException se l'azienda non è quotata in questa borsa.
     * @return il prezzo di un'azione dell'azienda nella borsa
     */
    public int getPrezzoAzione(Azienda azienda){
        Objects.requireNonNull(azienda, "L'azienda non può essere nulla");
        for (Iterator<Azione> it = azioni.iterator(); it.hasNext(); ) {
            Azione azione = it.next();
            if (azione.getAzienda().equals(azienda)){
                return azione.getPrezzo();
            }
        }
        throw new IllegalArgumentException("L'azienda non è quotata in questa borsa");
    }

    /**
     * Aggiunge una quotazione di un'{@link Azienda} nella {@link Borsa} con il prezzo e il numero di azioni specificati.
     * <p>
     * 
     * MODIFIES: this
     * EFFECTS: aggiunge una quotazione dell'azienda nella borsa
     * 
     * @param azienda l'azienda da quotare; non può essere {@code null}.
     * @param prezzo il prezzo dell'azione; deve essere maggiore di zero.
     * @param nAzioni il numero di azioni da quotare; deve essere maggiore di zero.
     * @throws NullPointerException se l'azienda è {@code null}.
     * @throws IllegalArgumentException se il prezzo è minore o uguale a zero.
     * @throws IllegalArgumentException se il numero di azioni è minore o uguale a zero.
     */
    public void aggiungiQuotazione(Azienda azienda, int prezzo, int nAzioni){
        Objects.requireNonNull(azienda, "L'azienda non può essere nulla");
        if (prezzo <= 0){
            throw new IllegalArgumentException("Il prezzo deve essere maggiore di zero");
        }
        if (nAzioni <= 0){
            throw new IllegalArgumentException("Il numero di azioni deve essere maggiore di zero");
        }
        Azione azione = new Azione(azienda, prezzo, nAzioni, nAzioni);
        azioni.add(azione);
    }

    /**
     * Acquista un numero specificato di {@link Azione} di una determinata {@link Azienda} nella {@link Borsa}.
     * <p>
     * 
     * MODIFIES: this, operatore
     * EFFECTS: acquista il numero specificato di azioni dell'azienda nella borsa
     * 
     * @param operatore l'operatore che acquista le azioni; non può essere {@code null}.
     * @param azienda l'azienda di cui acquistare le azioni; non può essere {@code null}.
     * @param nAzioni il numero di azioni da acquistare; deve essere maggiore di zero.
     * @throws NullPointerException se l'operatore è {@code null}.
     * @throws NullPointerException se l'azienda è {@code null}.
     * @throws IllegalArgumentException se il numero di azioni è minore o uguale a zero.
     * @throws IllegalArgumentException se l'azienda non è quotata in questa borsa.
     * @throws IllegalArgumentException se l'operatore non ha abbastanza azioni da vendere.
     */
    public void acquistaAzioni(Operatore operatore, Azienda azienda, int nAzioni){
        Objects.requireNonNull(operatore, "L'operatore non può essere nullo");
        Objects.requireNonNull(azienda, "L'azienda non può essere nulla");
        if (nAzioni <= 0){
            throw new IllegalArgumentException("Il numero di azioni deve essere maggiore di zero");
        }
        Azione azioneDaAcquistare = null;
        for (Iterator<Azione> it = azioni.iterator(); it.hasNext(); ) {
            Azione azione = it.next();
            if (azione.getAzienda().equals(azienda)){
                if (nAzioni > azione.getNDisponibili()){
                    throw new IllegalArgumentException("Non ci sono abbastanza azioni disponibili");
                }
                azioneDaAcquistare = azione;
                break;
            }
        }
        Objects.requireNonNull(azioneDaAcquistare, "L'azienda non è quotata in questa borsa");
        azioneDaAcquistare.vendi(nAzioni);
        Allocazione allocazione = operatori.getOrDefault(operatore, new Allocazione(new HashMap<>()));
        allocazione.incrementa(azioneDaAcquistare, nAzioni);
        operatori.put(operatore, allocazione);
        if (politicaDiPrezzo != null) {
            int prezzo = azioneDaAcquistare.getPrezzo();
            int nuovoPrezzo = politicaDiPrezzo.calcolaNuovoPrezzo(prezzo, nAzioni, true, azienda);
            azioneDaAcquistare.setPrezzo(nuovoPrezzo);
        }
    }

    /**
     * Vende un numero specificato di {@link Azione} di una determinata {@link Azienda} nella {@link Borsa}.
     * <p>
     * 
     * MODIFIES: this, operatore
     * EFFECTS: vende il numero specificato di azioni dell'azienda nella borsa
     * 
     * @param operatore l'operatore che vende le azioni; non può essere {@code null}.
     * @param azienda l'azienda di cui vendere le azioni; non può essere {@code null}.
     * @param nAzioni il numero di azioni da vendere; deve essere maggiore di zero.
     * @throws NullPointerException se l'operatore è {@code null}.
     * @throws NullPointerException se l'azienda è {@code null}.
     * @throws IllegalArgumentException se il numero di azioni è minore o uguale a zero.
     * @throws IllegalArgumentException se l'operatore non ha azioni in questa borsa.
     * @throws IllegalArgumentException se l'operatore non ha abbastanza azioni da vendere.
     * @throws IllegalArgumentException se l'azienda non è quotata in questa borsa.
     */
    public void vendiAzioni(Operatore operatore, Azienda azienda, int nAzioni){
        Objects.requireNonNull(operatore, "L'operatore non può essere nullo");
        Objects.requireNonNull(azienda, "L'azienda non può essere nulla");
        if (nAzioni <= 0){
            throw new IllegalArgumentException("Il numero di azioni deve essere maggiore di zero");
        }
        if (!operatori.containsKey(operatore)){
            throw new IllegalArgumentException("L'operatore non ha azioni in questa borsa");
        }
        Allocazione allocazione = operatori.get(operatore);
        Map<Azione, Integer> azion = allocazione.getAzioni();
        Azione azioneDaVendere = null;
        for (Iterator<Azione> it = azion.keySet().iterator(); it.hasNext(); ) {
            Azione azione = it.next();
            if (azione.getAzienda().equals(azienda)){
                if (nAzioni > azion.get(azione)){
                    throw new IllegalArgumentException("L'operatore non ha abbastanza azioni da vendere");
                }
                azioneDaVendere = azione;
                break;
            }
        }
        Objects.requireNonNull(azioneDaVendere, "L'azienda non è quotata in questa borsa");
        allocazione.decrementa(azioneDaVendere, nAzioni);
        if (allocazione.getAzioni().isEmpty()){
            operatori.remove(operatore);
        }
        azioneDaVendere.acquista(nAzioni);
        if (politicaDiPrezzo != null) {
            int prezzo = azioneDaVendere.getPrezzo();
            int nuovoPrezzo = politicaDiPrezzo.calcolaNuovoPrezzo(prezzo, nAzioni, false, azienda);
            azioneDaVendere.setPrezzo(nuovoPrezzo);
        }
    }

    /**
     * La classe Azione rappresenta un'azione quotata in una {@link Borsa}.
     * <p>
     * Ogni {@link Azione} è identificata dall'{@link Azienda} emittente, dal prezzo, dal numero di azioni disponibili e dal numero totale di azioni.
     * <p>
     * Gli oggetti di tipo {@link Azione} sono mutabili in quanto prezzo ed nDisponibili possono cambiare col tempo.
     * <p>
     * Garantisce che un'{@link Azione} sia creata solo con un'azienda valida, un prezzo positivo e un numero di azioni positivo.
     * <p>
     * La classe fornisce metodi per ottenere informazioni sull'azione e per aggiornare il numero di azioni disponibili, il prezzo e per acquistare e vendere azioni.
     * <p>
     * 
     * Rappresentazione dello stato:
     * - azienda: l'azienda emittente dell'azione, non può essere nulla.
     * - prezzo: il prezzo dell'azione, deve essere positivo.
     * - nDisponibili: il numero di azioni disponibili, deve essere positivo.
     * - nTotali: il numero totale di azioni di azienda in questa borsa, deve essere positivo.
     * 
     */
    public static class Azione{

        /*
         * Funzione di astrazione:
         * - AF(azienda, prezzo, nDisponibili, nTotali) = un'azione dell'azienda azienda con prezzo prezzo, numero di azioni disponibili nDisponibili e numero totale di azioni nTotali
         * - Un'istanza di Azione rappresenta un'azione di un azienda che si è quotata in una borsa con prezzo prezzo, numero di azioni disponibili nDisponibili e numero totale di azioni nTotali
         */

        /*
         * Invariante di rappresentazione:
         * - L'azienda emittente deve essere non nulla
         * - Il prezzo dell'azione deve essere positivo
         * - Il numero di azioni disponibili deve essere positivo
         * - Il numero totale di azioni deve essere positivo
         * - Il numero di azioni disponibili non può essere maggiore del numero totale di azioni
         */

        /**
         * L'{@link Azienda} emittente dell'azione.
         */
        private final Azienda azienda;

        /**
         * Il prezzo dell'azione.
         */
        private int prezzo;

        /**
         * Il numero di azioni disponibili.
         */
        private int nDisponibili;

        /**
         * Il numero totale di azioni.
         */
        private final int nTotali;

        /**
         * Crea un'istanza di {@link Azione} con l'{@link Azienda}, il prezzo, il numero di azioni disponibili e il numero totale di azioni specificati.
         * <p>
         * 
         * EFFECTS: crea una nuova azione con l'azienda, il prezzo, il numero di azioni disponibili e il numero totale di azioni specificati
         * 
         * @param azienda l'azienda emittente dell'azione; non può essere {@code null}.
         * @param prezzo il prezzo dell'azione; deve essere maggiore di zero.
         * @param nDisponibili il numero di azioni disponibili; deve essere maggiore di zero.
         * @param nTotali il numero totale di azioni; deve essere maggiore di zero.
         * @throws NullPointerException se l'azienda è {@code null}.
         * @throws IllegalArgumentException se il prezzo è minote o uguale a zero.
         * @throws IllegalArgumentException se il numero di azioni disponibi è minore o uguale a zero.
         * @throws IllegalArgumentException se il numero totale di azioni è minore o uguale a zero.
         */
        private Azione(Azienda azienda, int prezzo, int nDisponibili, int nTotali){
            if (Objects.requireNonNull(azienda, "L'azienda non può essere nulla") == null){
                throw new NullPointerException("L'azienda non può essere nulla");
            }
            if (prezzo <= 0){
                throw new IllegalArgumentException("Il prezzo deve essere maggiore di zero");
            }
            if (nDisponibili <= 0){
                throw new IllegalArgumentException("Il numero di azioni disponibili deve essere maggiore di zero");
            }
            if (nTotali <= 0){
                throw new IllegalArgumentException("Il numero di azioni totali deve essere maggiore di zero");
            }
            this.azienda = azienda;
            this.prezzo = prezzo;
            this.nDisponibili = nDisponibili;
            this.nTotali = nTotali;  
        }

        /**
         * Restituisce l'{@link Azienda} emittente dell'azione.
         * <p>
         * 
         * EFFECTS: restituisce l'azienda emittente dell'azione
         * 
         * @return l'azienda emittente dell'azione
         */
        public Azienda getAzienda(){
            return azienda;
        }

        /**
         * Restituisce il prezzo dell'azione.
         * <p>
         * 
         * EFFECTS: restituisce il prezzo dell'azione
         * 
         * @return il prezzo dell'azione
         */
        public int getPrezzo(){
            return prezzo;
        }

        /**
         * Restituisce il numero di azioni disponibili.
         * <p>
         * 
         * EFFECTS: restituisce il numero di azioni disponibili
         * 
         * @return il numero di azioni disponibili
         */
        public int getNDisponibili(){
            return nDisponibili;
        }

        /**
         * Restituisce il numero totale di azioni.
         * <p>
         * 
         * EFFECTS: restituisce il numero totale di azioni
         * 
         * @return il numero totale di azioni
         */
        public int getNTotali(){
            return nTotali;
        }

        /**
         * Imposta il prezzo dell'{@link Azione}.
         * <p>
         * 
         * MODIFIES: this
         * EFFECTS: imposta il prezzo dell'azione
         * 
         * @param prezzo il nuovo prezzo dell'azione; deve essere maggiore di zero.
         * @throws IllegalArgumentException se il prezzo è minore o uguale a zero.
         */
        private void setPrezzo(int prezzo) {
            if (prezzo <= 0) {
                throw new IllegalArgumentException("Il prezzo deve essere maggiore di zero");
            }
            this.prezzo = prezzo;
        }

        /**
         * Vende un numero specificato di {@link Azione}.
         * <p>
         * 
         * MODIFIES: this
         * EFFECTS: decrementa il numero di azioni disponibili del numero specificato
         * 
         * @param nAzioni il numero di azioni da vendere; deve essere maggiore di zero e minore o uguale al numero di azioni disponibili.
         * @throws IllegalArgumentException se il numero di azioni è minore o uguale a zero.
         * @throws IllegalArgumentException se il numero di azioni è maggiore del numero di azioni disponibili.
         */
        private void vendi(int nAzioni){
            if (nAzioni <= 0){
                throw new IllegalArgumentException("Il numero di azioni da vendere deve essere maggiore di zero");
            }
            if (nAzioni > nDisponibili){
                throw new IllegalArgumentException("Non ci sono abbastanza azioni disponibili");
            }
            nDisponibili -= nAzioni;
        }

        /**
         * Acquista un numero specificato di {@link Azione}.
         * <p>
         * 
         * MODIFIES: this
         * EFFECTS: incrementa il numero di azioni disponibili del numero specificato
         * 
         * @param nAzioni il numero di azioni da acquistare; deve essere maggiore di zero.
         * @throws IllegalArgumentException se il numero di azioni è minore o uguale a zero.
         */
        private void acquista(int nAzioni){
            if (nAzioni <= 0){
                throw new IllegalArgumentException("Il numero di azioni da acquistare deve essere maggiore di zero");
            }
            nDisponibili += nAzioni;
        }
    }

    /**
     * La classe Allocazione rappresenta l'allocazione di {@link Azione} di un {@link Operatore} in una {@link Borsa}.
     * <p>
     * Ogni {@link Allocazione} contiene una mappa delle azioni e delle quantità possedute dall'{@link Operatore}.
     * <p>
     * Gli oggetti di tipo {@link Allocazione} sono mutabili in quanto la mappa delle allocazioni può cambiare col tempo.
     * <p>
     * Garantisce che un'{@link Allocazione} sia creata solo con una mappa di allocazioni valida.
     * <p>
     * La classe fornisce metodi per ottenere le allocazioni e per incrementare o decrementare il numero di azioni possedute.
     * <p>
     * 
     * Rappresentazione dello stato:
     * - allocazioni: una mappa delle azioni e delle quantità possedute dall'operatore, non può essere nulla.
     * 
     */
    public static class Allocazione{

        /*
         * Funzione di astrazione:
         * - AF(allocazioni) = un'allocazione di azioni con la mappa delle azioni e delle quantità possedute dall'operatore
         * - Un'istanza di Allocazione rappresenta un'allocazione di azioni e quantità possedute dall'operatore
         */

        /*
         * Invariante di rappresentazione:
         * - La mappa delle allocazioni deve essere non nulla
         * - per ogni azione a in allocazioni, a != null
         * - per ogni quantità q in allocazioni, q > 0
         * - per ogni azione a in allocazioni, a non può essere duplicata
         * - per ogni azione a in allocazioni, a deve essere posseduta dall'operatore
         * - per ogni azione a in allocazioni, a deve essere quotata nella borsa
         * - per ogni azione a in allocazioni, a deve avere un prezzo valido
         * - per ogni azione a in allocazioni, a deve avere un numero di azioni disponibili valido
         * - per ogni azione a in allocazioni, a deve avere un numero totale di azioni valido
         */

        /**
         * La mappa delle {@link Azione} e delle quantità possedute dall'{@link Operatore}.
         */
        private final Map<Azione, Integer> allocazioni = new HashMap<>();

        /**
         * Crea un'istanza di {@link Allocazione} con la mappa delle allocazioni specificata.
         * <p>
         * 
         * EFFECTS: crea una nuova allocazione con la mappa delle allocazioni specificata
         * 
         * @param allocazioni la mappa delle allocazioni; non può essere {@code null}.
         * @throws NullPointerException se la mappa delle allocazioni è {@code null}.
         */
        private Allocazione(Map<Azione, Integer> allocazioni){
            if (Objects.requireNonNull(allocazioni, "Le allocazioni non possono essere nulle") == null){
                throw new NullPointerException("Le allocazioni non possono essere nulle");
            }
            this.allocazioni.putAll(allocazioni);
        }

        /**
         * Restituisce la mappa delle {@link Azione} e delle quantità possedute dall'{@link Operatore}.
         * <p>
         * 
         * EFFECTS: restituisce la mappa delle azioni e delle quantità possedute dall'operatore
         * 
         * @return la mappa delle azioni e delle quantità possedute dall'operatore
         */
        public Map<Azione, Integer> getAzioni(){
            return allocazioni;
        }

        /**
         * Incrementa il numero di azioni possedute dall'{@link Operatore} per una determinata {@link Azione}.
         * <p>
         * 
         * MODIFIES: this
         * EFFECTS: incrementa il numero di azioni possedute dall'operatore per l'azione specificata
         * 
         * @param azione l'azione di cui incrementare il numero di azioni possedute; non può essere {@code null}.
         * @param nAzioni il numero di azioni da incrementare; deve essere maggiore di zero.
         * @throws NullPointerException se l'azione è {@code null}.
         * @throws IllegalArgumentException se il numero di azioni è minore o uguale a zero.
         */
        private void incrementa(Azione azione, int nAzioni){
            Objects.requireNonNull(azione, "L'azione non può essere nulla");
            if (nAzioni <= 0){
                throw new IllegalArgumentException("Il numero di azioni da incrementare deve essere maggiore di zero");
            }
            allocazioni.put(azione, allocazioni.getOrDefault(azione, 0) + nAzioni);
        }

        /**
         * Decrementa il numero di azioni possedute dall'{@link Operatore} per una determinata {@link Azione}.
         * <p>
         * 
         * MODIFIES: this
         * EFFECTS: decrementa il numero di azioni possedute dall'operatore per l'azione specificata
         * 
         * @param azione l'azione di cui decrementare il numero di azioni possedute; non può essere {@code null}.
         * @param nAzioni il numero di azioni da decrementare; deve essere maggiore di zero.
         * @throws NullPointerException se l'azione è {@code null}.
         * @throws IllegalArgumentException se il numero di azioni è minore o uguale a zero.
         */
        private void decrementa(Azione azione, int nAzioni){
            Objects.requireNonNull(azione, "L'azione non può essere nulla");
            if (nAzioni <= 0){
                throw new IllegalArgumentException("Il numero di azioni da decrementare deve essere maggiore di zero");
            }
            int nuoveAzioni = allocazioni.get(azione) - nAzioni;
                if (nuoveAzioni <= 0){
                allocazioni.remove(azione);
            } else {
                allocazioni.put(azione, nuoveAzioni);
            }
        }
    }
    
    /**
     * L'interfaccia PoliticaDiPrezzo rappresenta una politica di prezzo applicata alle {@link Azione} in una {@link Borsa}.
     * <p>
     * Ogni implementazione di {@link PoliticaDiPrezzo} deve fornire un metodo per calcolare il nuovo prezzo di un'{@link Azione} in base a una transazione.
     */
    public interface PoliticaDiPrezzo {
        /**
         * Calcola il nuovo prezzo di un'{@link Azione} in base a una transazione.
         * 
         * REQUIRES: prezzoCorrente > 0, nAzioni > 0, azienda non nulla
         * EFFECTS: restituisce il nuovo prezzo dell'azione in base alla transazione
         * 
         * @param prezzoCorrente il prezzo corrente dell'azione; deve essere maggiore di zero.
         * @param nAzioni il numero di azioni coinvolte nella transazione; deve essere maggiore di zero.
         * @param acquisto true se la transazione è un acquisto, false se è una vendita.
         * @param azienda l'azienda emittente dell'azione; non può essere {@code null}.
         * @return il nuovo prezzo dell'azione in base alla transazione
         * @throws IllegalArgumentException se il prezzo corrente o il numero di azioni sono minori o uguali a zero.
         * @throws NullPointerException se l'azienda è {@code null}.
         */
        int calcolaNuovoPrezzo(int prezzoCorrente, int nAzioni, boolean acquisto, Azienda azienda);
    }

    /**
     * Implementazione di {@link PoliticaDiPrezzo} che non modifica il prezzo delle azioni.
     * <p>
     * Questa politica mantiene invariato il prezzo delle azioni
     */
    public static class NessunaPolitica implements PoliticaDiPrezzo {

        /**
         * Crea un'istanza di {@link NessunaPolitica}.
         * <p>
         * 
         * EFFECTS: crea una nuova politica di prezzo che mantiene invariato il prezzo delle azioni
         */
        public NessunaPolitica() {
        }

        @Override
        public int calcolaNuovoPrezzo(int prezzoCorrente, int quantita, boolean èAcquisto, Azienda azienda) {
            return prezzoCorrente;
        }
    }

    /**
     * Implementazione di {@link PoliticaDiPrezzo} che incrementa il prezzo delle azioni di un valore costante.
     */
    public class IncrementoCostante implements PoliticaDiPrezzo {

        /**
         * L'incremento del prezzo.
         */
        private final int incremento;

        /**
         * Crea un'istanza di {@link IncrementoCostante} con l'incremento specificato.
         * <p>
         * 
         * EFFECTS: crea una nuova politica di prezzo con l'incremento specificato
         * 
         * @param incremento il valore dell'incremento; deve essere maggiore di zero.
         * @throws IllegalArgumentException se l'incremento è minore o uguale a zero.
         */
        public IncrementoCostante(int incremento) {
            if (incremento <= 0) {
                throw new IllegalArgumentException("L'incremento deve essere maggiore di zero");
            }
            this.incremento = incremento;
        }

        @Override
        public int calcolaNuovoPrezzo(int prezzoCorrente, int nAzioni, boolean acquisto, Azienda azienda) {
            return acquisto ? prezzoCorrente + incremento : prezzoCorrente;
        }
    }

    /**
     * Implementazione di {@link PoliticaDiPrezzo} che decrementa il prezzo delle azioni di un valore costante fino a un prezzo minimo.
     */
    public class DecrementoCostante implements PoliticaDiPrezzo {

        /**
         * Il decremento del prezzo.
         */
        private final int decremento;

        /**
         * Il prezzo minimo dell'azione.
         */
        private final int prezzoMinimo;

        /**
         * Crea un'istanza di {@link DecrementoCostante} con il decremento e il prezzo minimo specificati.
         * <p>
         * 
         * EFFECTS: crea una nuova politica di prezzo con il decremento e il prezzo minimo specificati
         * 
         * @param decremento il valore del decremento; deve essere maggiore di zero.
         * @param prezzoMinimo il prezzo minimo dell'azione; deve essere maggiore o uguale a zero.
         * @throws IllegalArgumentException se il decremento è minore o uguale a zero.
         * @throws IllegalArgumentException se il prezzo minimo è minore di zero.
         */
        public DecrementoCostante(int decremento, int prezzoMinimo) {
            if (decremento <= 0 || prezzoMinimo < 0) {
                throw new IllegalArgumentException("Il decremento deve essere positivo e il prezzo minimo >= 0");
            }
            this.decremento = decremento;
            this.prezzoMinimo = prezzoMinimo;
        }

        @Override
        public int calcolaNuovoPrezzo(int prezzoCorrente, int nAzioni, boolean acquisto, Azienda azienda) {
            return acquisto ? prezzoCorrente : Math.max(prezzoCorrente - decremento, prezzoMinimo);
        }
    }

    /**
     * Implementazione di {@link PoliticaDiPrezzo} che varia il prezzo delle azioni di un valore costante.
     */
    public class VariazioneCostante implements PoliticaDiPrezzo {

        /**
         * La variazione del prezzo.
         */
        private final int variazione;

        /**
         * Crea un'istanza di {@link VariazioneCostante} con la variazione specificata.
         * <p>
         * 
         * EFFECTS: crea una nuova politica di prezzo con la variazione specificata
         * 
         * @param variazione il valore della variazione; deve essere maggiore di zero.
         * @throws IllegalArgumentException se la variazione è minore o uguale a zero.
         */
        public VariazioneCostante(int variazione) {
            if (variazione <= 0) {
                throw new IllegalArgumentException("La variazione deve essere maggiore di zero");
            }
            this.variazione = variazione;
        }

        @Override
        public int calcolaNuovoPrezzo(int prezzoCorrente, int nAzioni, boolean acquisto, Azienda azienda) {
            return acquisto ? prezzoCorrente + variazione : Math.max(prezzoCorrente - variazione, 1);
        }
    }

    /**
     * Implementazione di {@link PoliticaDiPrezzo} che varia il prezzo delle azioni in base a una soglia di quantità.
     */
    public class VariazioneSoglia implements PoliticaDiPrezzo {

        /**
         * La soglia di quantità.
         */
        private final int soglia;

        /**
         * Crea un'istanza di {@link VariazioneSoglia} con la soglia specificata.
         * <p>
         * 
         * EFFECTS: crea una nuova politica di prezzo con la soglia specificata
         * 
         * @param soglia il valore della soglia; deve essere maggiore di zero.
         * @throws IllegalArgumentException se la soglia è minore o uguale a zero.
         */
        public VariazioneSoglia(int soglia) {
            if (soglia <= 0) {
                throw new IllegalArgumentException("La soglia deve essere maggiore di zero");
            }
            this.soglia = soglia;
        }

        @Override
        public int calcolaNuovoPrezzo(int prezzoCorrente, int nAzioni, boolean acquisto, Azienda azienda) {
            if (nAzioni > soglia) {
                return acquisto ? prezzoCorrente * 2 : Math.max(prezzoCorrente / 2, 1);
            }
            return prezzoCorrente;
        }
    }

    /**
     * Implementazione di {@link PoliticaDiPrezzo} che varia il prezzo delle azioni in base alla presenza di una vocale nel nome della borsa o dell'azienda.
     */
    public class VariazioneVocali implements PoliticaDiPrezzo {

        /**
         * La lettera da considerare per la variazione del prezzo.
         */
        private final char lettera;

        /**
         * Crea un'istanza di {@link VariazioneVocali} con la lettera specificata.
         * <p>
         * 
         * EFFECTS: crea una nuova politica di prezzo con la lettera specificata
         * 
         * @param lettera la lettera da considerare per la variazione del prezzo.
         * @throws IllegalArgumentException se la lettera è nulla o vuota.
         */
        public VariazioneVocali(char lettera) {
            if (Character.toString(lettera).isBlank()) {
            throw new IllegalArgumentException("La lettera non può essere nulla o vuota");
            }
            this.lettera = Character.toLowerCase(lettera);
        }

        @Override
        public int calcolaNuovoPrezzo(int prezzoCorrente, int nAzioni, boolean acquisto, Azienda azienda) {
            if (isCoinvolta(nome) || isCoinvolta(azienda.getNome())) {
                return acquisto ? prezzoCorrente * 2 : Math.max(prezzoCorrente / 2, 1);
            }
            return prezzoCorrente;
        }

        /**
         * Verifica se la lettera è coinvolta nel nome.
         * <p>
         * 
         * EFFECTS: restituisce true se la lettera è coinvolta nel nome, false altrimenti
         * 
         * @param nome il nome da verificare; non può essere {@code null}.
         * @throws NullPointerException se il nome è {@code null}.
         * @return true se la lettera è coinvolta nel nome, false altrimenti
         */
        private boolean isCoinvolta(String nome) {
            Objects.requireNonNull(nome, "Il nome non può essere nullo");
            char firstChar = Character.toLowerCase(nome.charAt(0));
            return "aeiou".indexOf(firstChar) != -1 || firstChar == lettera;
        }
    }
        /**
         * Restituisce il nome della borsa.
         * <p>
         * 
         * EFFECTS: restituisce il nome della borsa
         * 
         * @return il nome della borsa
         * @throws NullPointerException se il nome della borsa è {@code null}.
         * @throws IllegalArgumentException se il nome della borsa è vuoto.
         */
        @Override
        public int compareTo(Borsa Borsa) throws NullPointerException, ClassCastException{
        Objects.requireNonNull(Borsa, "La borsa non può essere nulla");
        if (nome == null){
            throw new NullPointerException("Il nome della borsa non può essere nullo");
        }
        return nome.compareTo(Borsa.nome);
    }

    /**
     * Verifica se due oggetti sono uguali.
     * 
     * @param o l'oggetto da confrontare con questa borsa
     * @return {@code true} se l'oggetto è uguale a questa borsa, {@code false} altrimenti
     */
    @Override
    public boolean equals(Object o){
        Objects.requireNonNull(o, "L'oggetto non può essere nullo");
        if (this == o){
            return true;
        }
        if (!(o instanceof Borsa)){
            return false;
        }
        Borsa Borsa = (Borsa) o;
        return Objects.equals(nome, Borsa.nome);
    }

    /**
     * Restituisce l'hash code della borsa.
     * 
     * @return l'hash code della borsa
     */
    @Override
    public int hashCode(){ 
        return Objects.hash(nome);
    }
}