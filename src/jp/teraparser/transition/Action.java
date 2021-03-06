package jp.teraparser.transition;

import jp.teraparser.model.Token;

import java.util.EnumSet;
import java.util.Set;


/**
 * jp.teraparser.transition
 *
 * @author Hiroki Teranishi
 */
public enum Action {
    LEFT(3) {
        /**
         * Left
         * (s|i, j|b, A) => (s, j|b, A+(j,l,i))
         */
        @Override
        public State apply(State state) {
            Stack stack = state.stack.clone();
            Token head = state.getBufferHeadToken();
            Token dependent = state.tokens[stack.pop()];
            return new State(state, this, new Arc(head.id, dependent.id), stack, state.bufferHead);
        }
    },
    RIGHT(2) {
        /**
         * Right
         * (s|i, j|b, A) => (s|ij, b, A+(i,l,j))
         */
        @Override
        public State apply(State state) {
            Stack stack = state.stack.clone();
            Token head = state.getStackTopToken();
            Token dependent = state.getBufferHeadToken();
            stack.push(state.bufferHead);
            return new State(state, this, new Arc(head.id, dependent.id), stack, state.bufferHead + 1);
        }
    },
    SHIFT(1) {
        /**
         * Shift
         * (s, i|b, A) => (s|i, b, A)
         */
        @Override
        public State apply(State state) {
            Stack stack = state.stack.clone();
            stack.push(state.bufferHead);
            return new State(state, this, null, stack, state.bufferHead + 1);
        }
    },
    REDUCE(0) {
        /**
         * Reduce
         * (s|i, b, A) => (s, b, A)
         */
        @Override
        public State apply(State state) {
            Stack stack = state.stack.clone();
            stack.pop();
            return new State(state, this, null, stack, state.bufferHead);
        }
    };

    public final int index;
    public static int SIZE = values().length;

    Action(int index) {
        this.index = index;
    }

    public abstract State apply(State state);

    protected static Set<Action> getPossibleActions(State state) {
        Set<Action> actions = EnumSet.noneOf(Action.class);
        if (state.isTerminal()) {
            return actions;
        }
        Token stackTop = state.getStackTopToken();
        if (!state.stack.isEmpty()) {
            boolean canLeft = true;
            if (stackTop.isRoot()) {
                canLeft = false;
            }
            if (state.hasDependentArc(stackTop.id)) {
                canLeft = false;
                actions.add(Action.REDUCE);
            }
            if (canLeft) {
                actions.add(Action.LEFT);
            }
            actions.add(Action.RIGHT);
        }
        actions.add(Action.SHIFT);
        return actions;
    }
}
