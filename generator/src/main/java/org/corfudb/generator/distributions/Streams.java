package org.corfudb.generator.distributions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * This class implements a distribution over the possible streams that
 * can be created/modified.
 *
 * Created by maithem on 7/14/17.
 */
public class Streams implements DataSet {

    final Set<UUID> streamIds;
    final int numStreams;

    public Streams(int num) {
        streamIds = new HashSet<>();
        numStreams = num;
    }

    @Override
    public void populate() {
        for (int x = 0; x < numStreams; x++) {
            streamIds.add(UUID.randomUUID());
        }
    }

    @Override
    public List<UUID> getDataSet() {
        return new ArrayList<>(streamIds);
    }
}
