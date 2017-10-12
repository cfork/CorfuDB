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
	}
	if (args.length >= 2) {
		size = Integer.parseInt(args[1]);
	}
        CorfuRuntime runtime = getRuntimeAndConnect(corfuConfigurationString);
        UUID streamA = UUID.nameUUIDFromBytes("stream A".getBytes());
	String s = "helloworld";
	String payload = "";
	for (int i = 0; i < 400; i++) {
		payload += s;
	}
	byte[] testPayload = payload.getBytes(); // 4K size
        IStreamView sv = runtime.getStreamsView().get(streamA);
	for (int i = 0; i < size/10; i++) {
		sv.append(testPayload);
	}
	long startTime = System.nanoTime();
	for (int i = 0; i < size; i++) {
		sv.append(testPayload);
	}
	long et = System.nanoTime() - startTime;
	System.out.printf("time: %f ms, latency: %f ms/op, throughput: %f op/s\n", et/1e6, et/1e6/size, size*1e9/et);
    }
}
