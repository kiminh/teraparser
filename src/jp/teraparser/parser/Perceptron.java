package jp.teraparser.parser;

import jp.teraparser.model.Feature;
import jp.teraparser.transition.Action;
import jp.teraparser.transition.State;

import java.util.Collection;
import java.util.Set;

/**
 * jp.teraparser.parser
 *
 * @author Hiroki Teranishi
 */
public class Perceptron {
    private float[][] weights;
    private float[][] averagedWeights;

    private int count;

    public Perceptron() {
        this(new float[Action.SIZE][Feature.SIZE]);
    }

    public Perceptron(float[][] weights) {
        setWeights(weights);
    }

    public float[][] getWeights() {
        return weights;
    }

    public void setWeights(float[][] weights) {
        this.weights = weights;
        this.averagedWeights = weights.clone();
        this.count = 1;
    }

    public float[][] getAveragedWeights() {
        float[][] result = new float[weights.length][weights[0].length];
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                result[i][j] = weights[i][j] - averagedWeights[i][j] / count;
            }
        }
        return result;
    }

    public void incrementCount() {
        count++;
    }

    public double getScore(int[] featureIndexes, Action action) {
        double score = 0.0;
        for (int feature : featureIndexes) {
            score += weights[action.index][feature];
        }
        return score;
    }

    public Action getNextAction(State state) {
        Set<Action> actions = state.getPossibleActions();
        if (actions.size() == 0) {
            throw new IllegalStateException("Any action is not permitted.");
        } else if (actions.size() == 1) {
            return (Action) actions.iterator();
        }
        return classify(state.getFeatures(), actions);
    }

    public Action classify(int[] featureIndexes, Collection<Action> actions) {
        double bestScore = Double.NEGATIVE_INFINITY;
        Action bestAction = null;
        for (Action action : actions) {
            double score = getScore(featureIndexes, action);
            if (score > bestScore) {
                bestScore = score;
                bestAction = action;
            }
        }
        return bestAction;
    }

    public void update(State oracle, State predict) {
        while (predict.prevState != null) {
            updateWeight(weights[predict.prevAction.index], predict.prevState.getFeatures(), -1);
            updateWeight(averagedWeights[predict.prevAction.index], predict.prevState.getFeatures(), -count);
            predict = predict.prevState;
        }
        while (oracle.prevState != null) {
            updateWeight(weights[oracle.prevAction.index], oracle.prevState.getFeatures(), 1);
            updateWeight(averagedWeights[oracle.prevAction.index], oracle.prevState.getFeatures(), count);
            oracle = oracle.prevState;
        }
    }

    public void update(Action oracleAction, Action predictAction, int[] features) {
        updateWeight(weights[oracleAction.index], features, 1);
        updateWeight(averagedWeights[oracleAction.index], features, count);
        updateWeight(weights[predictAction.index], features, -1);
        updateWeight(averagedWeights[predictAction.index], features, -count);
    }

    private void updateWeight(float[] weight, int[] featureIndexes, float value) {
        for (int feature : featureIndexes) {
            weight[feature] += value;
        }
    }
}
