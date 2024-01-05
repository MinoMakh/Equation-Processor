package application;

public class CursorArray<T extends Comparable<T>> {

	public Node<T>[] list;

	public CursorArray(int len) {
		list = new Node[len];
		initialization();
	}

	// Initializing the list
	private void initialization() {
		for (int i = 0; i < list.length - 1; i++)
			list[i] = new Node<>(null, i + 1);
		list[list.length - 1] = new Node<>(null, 0);
	}

	// (Memory Allocation) Getting a free node from the free list
	public int malloc() {
		// If the list is full resize it
		if (list[0].getNext() == 0)
			resize();

		int p = list[0].getNext();
		list[0].setNext(list[p].getNext());

		return p;
	}

	// Resetting a node into the free list
	public void free(int p) {
		list[p] = new Node(null, list[0].getNext());
		list[0].setNext(p);
	}

	// Checking if a list is null
	public boolean isNull(int l) {
		return list[l] == null;
	}

	// Checking if a list is empty
	public boolean isEmpty(int l) {
		return list[l].getNext() == 0;
	}
	
	
	// Checking if a node is the last in its list
	public boolean isLast(int p) {
		return list[p].getNext() == 0;
	}

	// Creating a new list inside the cursor array
	public int createList() {
		int l = malloc();
		if (l == 0)
			System.out.println("The cursor array is full.");
		else
			list[l] = new Node(null, 0);
		return l; // Returning the new list's head
	}

	// Inserting a node at the head of a list
	public void insertAtHead(T data, int l) {
		if (isNull(l)) // List is not created
			return;
		int p = malloc();
		list[p] = new Node(data, list[l].getNext());
		list[l].setNext(p);

	}

	// Printing a list inside the cursor array
	public void traverseList(int l) {
		System.out.print("list: " + l + " -> ");
		while (!isNull(l) && !isEmpty(l)) {
			l = list[l].getNext();
			System.out.print(list[l] + " -> ");
		}
		System.out.println("Null");
	}

	// Finding a node with given list head
	public int find(T data, int l) {
		while (!isNull(l) && !isEmpty(l)) {
			l = list[l].getNext();
			if (list[l].getData().equals(data))
				return l;
		}
		return -1;
	}

	// Find the previous node given the list head
	public int findPrevious(T data, int l) {
		while (!isNull(l) && !isEmpty(l)) {
			if (list[list[l].getNext()].getData().compareTo(data) == 0)
				return l;
			l = list[l].getNext();
		}
		return -1;
	}

	// Deleting a node inside a list
	public Node<T> delete(T data, int l) {
		int p = findPrevious(data, l);
		if (p != -1) {
			int c = list[p].getNext();
			Node<T> temp = list[c];
			list[p].setNext(temp.getNext());
			free(c);
			return temp;
		}
		return null;
	}

	// Deleting the first item in a list
	public Node<T> deleteFirst(int l) {
		if (!isEmpty(l)) {
			int head = list[l].getNext();
			Node<T> deletedNode = list[head];
			list[l].setNext(deletedNode.getNext());
			free(head);
			return deletedNode;
		}
		return null;
	}

	// Getting the length of a list
	public int length(int l) {
		int count = 0;
		while (!isNull(l) && !isEmpty(l)) {
			l = list[l].getNext();
			count++;
		}
		return count;
	}

	// Getting the first item of a list
	public Node<T> findFirst(int l) {
		return list[list[l].getNext()];
	}

	// Clearing the list
	public void clear() {
		for (int i = 0; i < list.length; i++)
			list[i] = null;
	}

	// Creating the same CursorArray with double the length
	public void resize() {
		System.out.println("Resizing...");
		Node<T>[] newList = new Node[list.length * 2];

		// Copying the old data into the new cursor array
		for (int i = 0; i < list.length; i++) {
			if (list[i] != null)
				newList[i] = new Node<T>(list[i].getData(), list[i].getNext());
		}

		// Initializing the new list
		for (int i = list.length; i < newList.length-1; i++)
			newList[i] = new Node<>(null, i + 1);
		newList[newList.length - 1] = new Node(null, 0);
		newList[0].setNext(list.length);

		list = newList;
	}

	// Swapping two keys in a list
	public void swapNodes(T data1, T data2, int l) {
		// Getting the indexes
		int index1 = find(data1, l);
		int index2 = find(data2, l);

		// Checking if they are in the list
		if (index1 == -1) {
			System.out.println(data1 + " is not in the list.");
			return;
		}
		if (index2 == -1) {
			System.out.println(data2 + " is not in the list.");
			return;
		}

		// Getting the previous
		int prev1 = findPrevious(data1, l);
		int prev2 = findPrevious(data2, l);

		// Getting the next's in order
		int next1 = list[index1].getNext();
		int next2 = list[index2].getNext();

		// If the two nodes are adjacent
		if (list[list[index1].getNext()] == list[index2]) {
			list[index2].setNext(index1);
			list[prev2].setNext(index1);
			list[index1].setNext(next2);
			list[prev1].setNext(index2);
		} else if (list[list[index2].getNext()] == list[index1]) {
			list[index1].setNext(index2);
			list[prev1].setNext(index2);
			list[index2].setNext(next1);
			list[prev2].setNext(index1);
		}

		// If one of them are after the head
		else if (list[prev1].getData() == null) {
			list[l].setNext(index2);
			list[index2].setNext(next1);
			list[prev2].setNext(index1);
			list[index1].setNext(next2);
		} else if (list[prev2].getData() == null) {
			list[l].setNext(index1);
			list[index1].setNext(next2);
			list[prev1].setNext(index2);
			list[index2].setNext(next1);
		}

		// If they are not adjacent
		else {
			list[prev1].setNext(index2);
			list[index2].setNext(next1);
			list[prev2].setNext(index1);
			list[index1].setNext(next2);
		}
	}
}
