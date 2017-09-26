package org.corfudb.runtime.object;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** The interface for an object interfaced with SMR.
 * @param <T> The type of the underlying object.
 * Created by mwei on 11/10/16.
 */
public interface ICorfuSMR<T> {

    /** The suffix for all precompiled SMR wrapper classes. */
    String CORFUSMR_SUFFIX = "$CORFU";

    /** Get the proxy for this wrapper, to manage the state of the object.
     * @return The proxy for this wrapper. */
    ICorfuSMRProxy<T> getCorfuSMRProxy();

    /** Set the proxy for this wrapper, to manage the state of the object.
     * @param proxy The proxy to set for this wrapper. */
    void setCorfuSMRProxy(ICorfuSMRProxy<T> proxy);

    /** Get a map from strings (function names) to SMR upcalls.
     * @return The SMR upcall map. */
    Map<String, ICorfuSMRUpcallTarget<T>> getCorfuSMRUpcallMap();

    /** Get a map from strings (function names) to undo methods.
     * @return The undo map. */
    Map<String, IUndoFunction<T>> getCorfuUndoMap();

    /** Get a map from strings (function names) to undoRecord methods.
     * @return The undo record map. */
    Map<String, IUndoRecordFunction<T>> getCorfuUndoRecordMap();

    /** Get a map from strings (function names) to conflict functions.
     * @return The conflict entry map.
     */
    Map<String, IConflictFunction> getCorfuEntryToConflictMap();

    /** Get a set of strings (function names) which result in a reset
     * of the object.
     * @return  The set of strings that cause a reset on the object.
     */
    Set<String> getCorfuResetSet();

    /** Return the stream ID that this object belongs to.
     * @return The stream ID this object belongs to. */
    default UUID getCorfuStreamID() {
        return getObjectManager$CORFU().getBuilder().getStreamId();
    }

    /** Get the object builder used to generate this wrapper.
     * @return An object builder for this wrapper.
     */
    default IObjectBuilder<T> getCorfuBuilder() {
        return getObjectManager$CORFU().getBuilder();
    }

    /** Get the object manager for this wrapper.
     * @return An object manager for this wrapper.
     */
    IObjectManager<T> getObjectManager$CORFU();

    /** Get a state machine engine.
     * @return
     * */
    default IStateMachineEngine getEngine$CORFU() {
        return getObjectManager$CORFU().getEngine();
    }
}
