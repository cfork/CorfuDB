package org.corfudb.benchmarks;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.runtime.view.stream.IStreamView;
import org.corfudb.util.GitRepositoryState;

import java.util.UUID;

public class Bench {

        private static String config = "localhost:9000";
	private static int size = 10 * 1000;
	private static int count = size;
	private static int nthread = 1;
	private static Object lock = new Object();

	private static class Client implements Runnable {
		private IStreamView sv = null;
		private byte[] testPayload;

		public Client(String config) {
			CorfuRuntime runtime = getRuntimeAndConnect(config);
			UUID streamA = UUID.nameUUIDFromBytes("stream A".getBytes());
			String s = "helloworld";
			String payload = "";
			for (int i = 0; i < 400; i++) {
				payload += s;
			}
			testPayload = payload.getBytes(); // 4K size
			sv = runtime.getStreamsView().get(streamA);
		}

		public void run() {
			while (true) {
				synchronized (lock) {
					if (count > 0) {
						count --;
					} else {
						return;
					}
				}
				sv.append(testPayload);
			}

		}

	}

    private static CorfuRuntime getRuntimeAndConnect(String configurationString) {

        CorfuRuntime corfuRuntime = new CorfuRuntime(configurationString).connect();
        return corfuRuntime;
    }

    public static void main(String[] args) {
	if (args.length >= 1) {
		config = args[0];
	}
	if (args.length >= 2) {
		size = Integer.parseInt(args[1]);
		count = size;
	}
	if (args.length >= 3) {
		nthread = Integer.parseInt(args[2]);
	}
	Thread[] clients = new Thread[nthread];
	for (int i = 0; i < nthread; i++) {
		clients[i] = new Thread(new Client(config));
	}

	long startTime = System.nanoTime();
	for (int i = 0; i < nthread; i++) {
		clients[i].start();
	}
	for (int i = 0; i < nthread; i++) {
		try {
			clients[i].join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	long et = System.nanoTime() - startTime;

	System.out.printf("nthread: %d, time: %f ms, latency: %f ms/op, throughput: %f op/s\n", nthread, et/1e6, et/1e6/size*nthread, size*1e9/et);
    }
}
