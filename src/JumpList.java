public class JumpList {

    private SortedLinkedList list;

    /**
     * Begin with an empty JumpList
     */
    JumpList() {
        this(new SortedLinkedList());
    }

    /**
     * Builds a JumpList from a sorted linked list
     * @param ll
     */
    JumpList(SortedLinkedList ll) {
        list = ll;
        buildJumpList();
    }

    /**
     * Rebuilds entire JumpList from scratch
     */
    void buildJumpList() {
        buildJumpList(header(), size());
    }

    /**
     *
     * @param u header node of sublist
     * @param n number elements in sublist
     * @returns the node at position n in the sublist, with the guarantee that the nodes between u (inclusive)
     *          and n (exclusive) have had their jump pointers initialized
     */
    Node buildJumpList(Node u, int n) {
        if (n == 1) {
            return u;
        }

        // uniformly random element from {2, ..., n}
        int k = generateRandomNumberBetweenTwoAnd(n);
        // recursively build the list between our current pointer and the node we will jump to
        Node p = buildJumpList(u.next, k-1);
        // set jump to the returned node, and give it size k
        u.jump = p;
        u.jumpSize = k;
        // now recursively build the second part from the jump node to the end
        Node q = buildJumpList(p, n - k + 1);
        return q;
    }

    /**
     * Searches for the node with value x
     * @param x integer to search for
     * @return the largest node y in the JumpList such that y <= x
     */
    Node search(int x) {
        return searchWithIndex(x).node;
    }

    /**
     * Searches for the node with value x
     * @param x integer to search for
     * @return the largest node y in the JumpList such that y <= x
     *          AND the index it appears in the JumpList
     * @see NodeIndexPair NodeIndexPair for return type
     */
    NodeIndexPair searchWithIndex(int x) {
        // start with the header and we keep track of the index we are at
        Node u = header();
        int index = 1;

        while (u.next != null) {
            if (u.jump.value <= x) {
                // follow the jump pointer, index increases by how many nodes we jump over
                index += u.jumpSize - 1;
                u = u.jump;
            }
            else if (u.next.value <= x) {
                // follow the next pointer, index increases by one
                u = u.next;
                index++;
            }
            else {
                return new NodeIndexPair(u, index);
            }
        }
        return new NodeIndexPair(u, index);
    }

    /**
     * Inserts the element into the list if it doesn't already exist
     * @param x the integer to insert
     * @return the inserted node
     */
    Node insert(int x) {
        NodeIndexPair pair = searchWithIndex(x);
        Node y = pair.node;
        if (y.value == x) return y;   // we don't want duplicates
        Node insertedNode = list.insert(y, x);  // do a linked list insert operation - O(1) time
        // now deal with the jump pointers
        Node u = header();
        int n = size();
        // make a wrapper object, with an index one higher than the prev node
        NodeIndexPair insertedPair = new NodeIndexPair(insertedNode, pair.index + 1);
        setJumpOnInsert(u, insertedPair, n);
        return u;
    }

    /**
     * Deletes the element x if it exists.
     * @param x the element to delete
     * @return the deleted element, or null if it does not exist
     */
    Node delete(int x) {
        NodeIndexPair prevNode = searchWithIndex(x-1);
        Node y = prevNode.node;
        if (y.next == null || y.next.value != x) {
            return null;     // y isn't in the list
        }

        Node deleted = list.delete(y, y.next);     // linked list delete
        if (deleted == null) {
            return null;
        }

        Node u = header();
        int n = size();
        // Start at the head of list, and continue until we surpass x
        while (u.value <= x) {
            // if u jumps to x, rebuild the list between u and x
            if (u.jump.value == x) {
                u.jump = null;
                u.jumpSize = 1;
                buildJumpList(u, n);
                return deleted;
            }
            // follow the jump pointer
            else if (u.jump.value < x) {
                n = n - u.jumpSize + 1;
                u = u.jump;
            }
            // follow the next pointer
            else {
                u.jumpSize--;
                n = u.jumpSize - 1;
                u = u.next;
            }
        }
        return deleted;
    }

    private void setJumpOnInsert(Node u, NodeIndexPair pair, int n) {
        Node x = pair.node;
        if (u == x) {
            // if we get to the inserted node without a re-balance, then just set jump to next
            buildJumpList(u, n);
            return;
        }
        // uniformly random element from {2, ...., n}
        int k = generateRandomNumberBetweenTwoAnd(n);
        if (k == 2) {
            // occurs with probability 1/(n-1)
            // set jump to x
            u.jump = x;
            u.jumpSize = pair.index;    // overall size is the current index of x

            // recursively build the two partitions of the list
            buildJumpList(u.next, pair.index - 1);
            buildJumpList(u.jump, n - pair.index + 1);
        }
        else if (u.jump.value <= x.value) {
            // follow the jump pointer
            pair.index = pair.index - u.jumpSize + 1;   // reduces index by number of nodes we jump over
            // reduce subspace by  number of nodes we jump over
            setJumpOnInsert(u.jump, pair, n - u.jumpSize + 1);
        }
        else {
            // follow the next
            // x is contained in the current node u jump list, so increment size
            u.jumpSize++;
            pair.index--;   // index of x decreases by one
            // reduce subspace to the current jumplist
            setJumpOnInsert(u.next, pair, u.jumpSize - 1);
        }

    }

    int size() {
        return list.size();
    }

    Node header() {
        return list.header();
    }

    @Override
    public String toString() {
        Node u = header();
        StringBuilder toReturn = new StringBuilder("[");
        String prefix = "";
        while (u != null) {
            toReturn.append(prefix);
            toReturn.append("(");
            toReturn.append(u.toString());
            toReturn.append(" -> ");
            toReturn.append(u == null ? "nil" : u.jump);
            toReturn.append(" {"+ u.jumpSize + "}");
            toReturn.append(")");
            prefix = ", ";
            u = u.next;
        }
        toReturn.append("]");
        return toReturn.toString();
    }

    private int generateRandomNumberBetweenOneAnd(int n) {
        return (int)Math.ceil(Math.random() * (n));
    }

    private int generateRandomNumberBetweenTwoAnd(int n) {
        return (int)Math.ceil(Math.random() * (n-1)) + 1;
    }

    /**
     * Wrapper class that stores a Node and the index it appears in the JumpList
     */
    class NodeIndexPair {
        Node node;
        int index;

        NodeIndexPair(Node n, int index) {
            this.node = n;
            this.index = index;
        }

        @Override
        public String toString() {
            return "{node: " + node.value + ", index: " + this.index + "}";
        }
    }

    /*
    Ensures the JumpList is valid
     */
    boolean isValid() {
        // walk along the jump list and make sure all three properties are satisfied
        Node u = header();
        int smallestJumpValue = Integer.MAX_VALUE;  // the smallest jump value we've visited
        while (u.next != null) {
            if (u.value >= u.next.value) return false;  //property 1
            if (u.value >= u.jump.value) return false;  //property 2
            // we reset the jump value if we reach the current smallestJumpValue, or it gets smaller
            if (u.value == smallestJumpValue || u.jump.value < smallestJumpValue) {
                smallestJumpValue = u.jump.value;
            }
            // we fail if the jump pointer of our current node exceeds the smallestJumpValue
            // this violates the nesting property
            else if (smallestJumpValue < u.jump.value) {
                return false; //property 3
            }
            u = u.next;
        }

        return true;
    }
}
