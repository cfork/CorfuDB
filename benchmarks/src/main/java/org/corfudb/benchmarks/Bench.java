package org.corfudb.benchmarks;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.runtime.view.stream.IStreamView;
import org.corfudb.util.GitRepositoryState;

import java.util.UUID;

public class Bench {
    /**
     * Internally, the corfuRuntime interacts with the CorfuDB service over TCP/IP sockets.
     *
     * @param configurationString specifies the IP:port of the CorfuService
     *                            The configuration string has format "hostname:port", for example, "localhost:9090".
     * @return a CorfuRuntime object, with which Corfu applications perform all Corfu operations
     */
    private static CorfuRuntime getRuntimeAndConnect(String configurationString) {

        CorfuRuntime corfuRuntime = new CorfuRuntime(configurationString).connect();
        return corfuRuntime;
    }

    public static void main(String[] args) {

        String corfuConfigurationString = "localhost:9000";
	int size = 10 * 1000;
	if (args.length >= 1) {
		corfuConfigurationString = args[0];
		System.out.println("Connecting to " + corfuConfigurationString);
	}
	if (args.length >= 2) {
		size = Integer.parseInt(args[1]);
		System.out.println("Size of test " + size);
	}

        /**
         * First, the application needs to instantiate a CorfuRuntime,
         * which is a Java object that contains all of the Corfu utilities exposed to applications.
         */
        CorfuRuntime runtime = getRuntimeAndConnect(corfuConfigurationString);

        /**
         * Obviously, this application is not doing much yet,
         * but you can already invoke getRuntimeAndConnect to test if you can connect to a deployed Corfu service.
         *
         * Above, you will need to point it to a host and port which is running the service.
         * See {@link https://github.com/CorfuDB/CorfuDB} for instructions on how to deploy Corfu.
         */
        UUID streamA = UUID.nameUUIDFromBytes("stream A".getBytes());
        byte[] testPayload = "hello world".getBytes();
        IStreamView sv = runtime.getStreamsView().get(streamA);
	for (int i = 0; i < size; i++) {
		sv.append(testPayload);
	}
    }
}
