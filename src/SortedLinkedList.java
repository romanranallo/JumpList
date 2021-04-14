public class SortedLinkedList {

    private static int MINUS_INFTY = Integer.MIN_VALUE;

    private Node header;
    private int size;
    private int maxValue;
    private Node tail; // NOT ACCESIBLE

    SortedLinkedList() {
        header = new Node(MINUS_INFTY);
        size = 1;
        maxValue = MINUS_INFTY;
        tail = header;
    }

    void insert(int x) {
        Node prevNode = search(x);
        if (prevNode.value == x) return;
        insert(prevNode, x);
    }

    Node insert(Node prev, int x) {
        Node toInsert = new Node(x);
        toInsert.next = prev.next;
        prev.next = toInsert;
        size++;
        if (x > maxValue)
        {
            maxValue = x;
            tail = toInsert;

        }
        return toInsert;
    }

    /*
    Returns the deleted node x, or null if it does not exist. Do not delete the header.
     */
    Node delete(int x) {
        if (size == 1 || x == MINUS_INFTY) {
            return null;
        }

        Node curr = header;
        Node prev = null;

        while(curr.next != null && curr.next.value <= x) {
            prev = curr;
            curr = curr.next;
        }

        if (curr.value == x)
        {
            return delete(prev, curr);
        }
        return null;
    }

    Node delete(Node prev, Node x) {
        if (x.value == maxValue) {
            maxValue = prev.value;
            tail = prev;
        }
        prev.next = x.next;
        size--;
        return x;
    }

    // fails if x <= maxValue
    void insertAtEnd(int x) {
        if (x <= maxValue) {
            return;
        }
        maxValue = x;
        Node insert = new Node(x);
        tail.next = insert;
        tail = insert;
        size++;
    }

    /*
    Returns the largest node y such that y < x
     */
    Node search(int x) {
        Node u = header;
        while (u.next != null && u.next.value <= x) {
            u = u.next;
        }
        return u;
    }

    @Override
    public String toString() {
        Node u = header;
        StringBuilder toReturn = new StringBuilder("[");
        String prefix = "";
        while (u != null) {
            toReturn.append(prefix);
            toReturn.append(u.toString());
            prefix = ", ";
            u = u.next;
        }
        toReturn.append("]");
        return toReturn.toString();
    }

    int size() {
        return this.size;
    }

    Node header() {
        return this.header;
    }


}
