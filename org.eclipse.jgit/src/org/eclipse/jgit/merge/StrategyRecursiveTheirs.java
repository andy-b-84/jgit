package org.eclipse.jgit.merge;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;

import java.io.IOException;

/**
 * A one-way merge strategy overriding the origin's contents when necessary
 * Usecase :
 * We have a branch "toRebase" which we want to rebase onto "master", after "master" has advanced one commit ahead.
 * This commit (commit1) adds a file "test" with the content "git is beautiful".
 * Our branch "toRebase" adds the same file "test", but with the content "beautiful is git".
 * We want the contents of "toRebase" to override any conflicting modifications from master.
 * Using CLI git, we would issue :
 * $ git checkout toRebase
 * $ git rebase master --strategy=recursive --strategy-option=theirs
 */
public class StrategyRecursiveTheirs extends StrategyRecursive {
    private final String strategyName;

    private final int treeIndex;

    /**
     * Create a new merge strategy to select a specific input tree.
     *
     * @param name
     *            name of this strategy.
     */
    public StrategyRecursiveTheirs(final String name) {
        strategyName = name;
        treeIndex = 1;
    }

    @Override
    public String getName() {
        return strategyName;
    }

    @Override
    public ThreeWayMerger newMerger(final Repository db) {
        return new OneSide(db, treeIndex);
    }

    @Override
    public ThreeWayMerger newMerger(final Repository db, boolean inCore) {
        return new OneSide(db, treeIndex);
    }

    static class OneSide extends ResolveMerger {
        private final int treeIndex;

        protected OneSide(final Repository local, final int index) {
            super(local);
            treeIndex = index;
        }

        @Override
        protected boolean mergeImpl() throws IOException {
            return treeIndex < sourceTrees.length;
        }

        @Override
        public ObjectId getResultTreeId() {
            return sourceTrees[treeIndex];
        }

        @Override
        public ObjectId getBaseCommitId() {
            return null;
        }
    }
}
