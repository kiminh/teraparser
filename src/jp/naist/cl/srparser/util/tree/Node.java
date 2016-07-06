package jp.naist.cl.srparser.util.tree;

import java.util.ArrayList;

/**
 * jp.naist.cl.srparser.util.tree
 *
 * @author Hiroki Teranishi
 */
public class Node<T> implements Component<T> {
    protected T data;
    protected ArrayList<Component> children = new ArrayList<>();

    public Node(T data) {
        this.data = data;
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public void setData(T data) {
        this.data = data;
    }

    public void addChild(Component component) {
        children.add(component);
    }

    public Component getChild(int index) {
        return children.get(index);
    }

    public Component getLastChild() {
        return children.get(children.size() - 1);
    }

    public Component removeLastChild() {
        return children.remove(children.size() - 1);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(" + data);
        for (Component child : children) {
            builder.append(" " + child );
        }
        builder.append(")");
        return builder.toString();
    }
}