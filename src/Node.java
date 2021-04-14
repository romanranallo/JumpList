public class Node {

    int value;
    Node next;
    Node jump;
    int jumpSize;

    public Node(int value) {
        this.value = value;
        this.jump = null;
        this.jumpSize = 1;  // a single node is a list of size 1
    }

    public String toString() {
        return value == Integer.MIN_VALUE ? "-infty" : String.valueOf(value);
    }

    @Override
    public boolean equals(Object x) {
        if (x instanceof Node) {
            return ((Node)x).value == this.value;
        }
        return super.equals(x);
    }
}
