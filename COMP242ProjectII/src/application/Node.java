package application;

public class Node<T extends Comparable<T>> {

	private T data;
	private int next;

	public Node(T data, int next) {
		this.data = data;
		this.next = next;
	}

	@Override
	public String toString() {
		return "Data: " + data;
	}

	// Setters and Getters
	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public int getNext() {
		return next;
	}

	public void setNext(int next) {
		this.next = next;
	}

}
