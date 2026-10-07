package poker;
import java.util.NoSuchElementException;

/**
 * A generic singly linked chain.
 * 
 * @param <T>
 *            the element type
 */
public class LinkedChain<T>
{

    /** Private nested node class */
    private static class Node<T>
    {
        private T data;
        private Node<T> next;

        Node(T data)
        {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> head;   // first node (top of the deck); null when empty
    private Node<T> tail;   // last node, kept so add() is O(1)
    private int size;

    /**
     * Creates an empty chain.
     */
    public LinkedChain()
    {
        head = null;
        tail = null;
        size = 0;
    }


    /**
     * Appends item to the end.
     * 
     * @param item
     *            The item being added
     */
    public void add(T item)
    {
        checkNotNull(item);
        Node<T> newNode = new Node<>(item);
        if (isEmpty())
        {
            head = newNode;
            tail = newNode;
        }
        else
        {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }


    /**
     * Inserts item at the front.
     * 
     * @param item
     *            The item being added
     */
    public void addFirst(T item)
    {
        checkNotNull(item);
        Node<T> newNode = new Node<>(item);
        newNode.next = head;
        head = newNode;
        if (tail == null)
        {
            tail = newNode;
        }
        size++;
    }


    /**
     * Returns the first element without removing it.
     * 
     * @throws NoSuchElementException
     *             if the chain is empty
     */
    public T peek()
    {
        if (isEmpty())
        {
            throw new NoSuchElementException("Chain is empty");
        }
        return head.data;
    }


    /**
     * Returns the element at index (0 = head) without removing it.
     * 
     * @param index
     *            The index of the item you want to get
     * @return T at index
     * @throws IndexOutOfBoundsException
     *             if index < 0 or index >= size
     */
    public T get(int index)
    {
        return nodeAt(index).data;
    }


    /**
     * Replaces the element at index and returns the old one.
     * 
     * @param index
     *            The index you want to change
     * @param item
     *            The item to add
     * @return The item at the index before changing
     * @throws IndexOutOfBoundsException
     *             if index < 0 or index >= size
     * @throws IllegalArgumentException
     *             if item is null
     */
    public T set(int index, T item)
    {
        checkNotNull(item);
        Node<T> node = nodeAt(index);
        T old = node.data;
        node.data = item;
        return old;
    }


    /**
     * Returns the index of the first element equal to item (0 = head), or -1 if
     * it is not in the chain. getIndex(null) is -1.
     * 
     * @param item
     *            the item to get the index of
     * @return the index item is at
     */
    public int getIndex(T item)
    {
        if (item == null)
        {
            return -1;
        }
        int index = 0;
        Node<T> current = head;
        while (current != null)
        {
            if (item.equals(current.data))
            {
                return index;
            }
            index++;
            current = current.next;
        }
        return -1;
    }


    /**
     * True if any element equals(item). contains(null) is false.
     * 
     * @param item
     *            Item to find
     * @return Boolean indicating if the LinkedChain contains
     */
    public boolean contains(T item)
    {
        return count(item) > 0;
    }


    /**
     * Number of elements equal to item. count(null) is 0.
     * 
     * @param item
     *            Item to search for
     * @return Item count
     */
    public int count(T item)
    {
        if (item == null)
        {
            return 0;
        }
        int total = 0;
        Node<T> current = head;
        while (current != null)
        {
            if (item.equals(current.data))
            {
                total++;
            }
            current = current.next;
        }
        return total;
    }


    /**
     * Number of elements.
     * 
     * @return size of the list
     */
    public int size()
    {
        return size;
    }


    /**
     * True when the chain has no elements.
     * 
     * @return boolean indicating if the chain is empty or not
     */
    public boolean isEmpty()
    {
        return size == 0;
    }


    /**
     * Removes and returns the first element (deals the top card).
     * 
     * @return The element that was removed
     * @throws NoSuchElementException
     *             if the chain is empty
     */
    public T removeFirst()
    {
        if (isEmpty())
        {
            throw new NoSuchElementException("Chain is empty");
        }
        T data = head.data;
        head = head.next;
        size--;
        if (head == null)
        {
            tail = null;
        }
        return data;
    }


    /**
     * Removes the first element equal to item. Returns false (and changes
     * nothing) if not found or item is null.
     * 
     * @param item
     *            Item to remove
     * @return Boolean indicating whether the item was removed or not
     */
    public boolean remove(T item)
    {
        if (item == null || isEmpty())
        {
            return false;
        }
        if (item.equals(head.data))
        {
            removeFirst();
            return true;
        }
        Node<T> previous = head;
        Node<T> current = head.next;
        while (current != null)
        {
            if (item.equals(current.data))
            {
                unlinkAfter(previous);
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }


    /**
     * Removes and returns the element at index.
     * 
     * @param index
     *            where to remove an item
     * @return element that used to be at the index
     * @throws IndexOutOfBoundsException
     *             if index < 0 or index >= size
     */
    public T removeAt(int index)
    {
        checkIndex(index);
        if (index == 0)
        {
            return removeFirst();
        }
        Node<T> previous = nodeAt(index - 1);
        return unlinkAfter(previous);
    }


    /**
     * Removes every element.
     */
    public void clear()
    {
        head = null;
        tail = null;
        size = 0;
    }


    /**
     * Copies the elements, in order, into a new array. Usage: Card[] cards =
     * chain.toArray(new Card[0]);
     * 
     * @return Array version of the LinkedList
     */
    @SuppressWarnings("unchecked")
    public T[] toArray()
    {
        T[] result = (T[])new Object[size];
        int i = 0;
        Node<T> current = head;
        while (current != null)
        {
            result[i] = current.data;
            i++;
            current = current.next;
        }
        return result;
    }


    /**
     * Elements in order
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("[");
        Node<T> current = head;
        while (current != null)
        {
            sb.append(current.data);
            if (current.next != null)
            {
                sb.append(", ");
            }
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }


    /**
     * Walks to the node at index.
     */
    private Node<T> nodeAt(int index)
    {
        checkIndex(index);
        Node<T> current = head;
        for (int i = 0; i < index; i++)
        {
            current = current.next;
        }
        return current;
    }


    /**
     * Removes the node after previous and returns its data. Fixes tail if
     * needed.
     */
    private T unlinkAfter(Node<T> previous)
    {
        Node<T> removed = previous.next;
        previous.next = removed.next;
        if (removed == tail)
        {
            tail = previous;
        }
        size--;
        return removed.data;
    }


    private void checkIndex(int index)
    {
        if (index < 0 || index >= size)
        {
            throw new IndexOutOfBoundsException(
                "Index " + index + " out of range for size " + size);
        }
    }


    private void checkNotNull(T item)
    {
        if (item == null)
        {
            throw new IllegalArgumentException(
                "Cannot add null to a LinkedChain");
        }
    }
}
