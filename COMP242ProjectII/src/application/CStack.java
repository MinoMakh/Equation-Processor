package application;

public class CStack<T extends Comparable<T>> {

	private CursorArray<T> list;

	public CStack(int len) {
		list = new CursorArray<>(len);
	}

	public int createList() {
		return list.createList();
	}

	public void push(T data, int l) {
		list.insertAtHead(data, l);
	}

	public T pop(int l) {
		if (peek(l) != null)
			return (T) list.deleteFirst(l).getData();
		return null;
	}

	public T peek(int l) {
		return list.findFirst(l).getData();
	}

	public boolean isEmpty(int l) {
		return list.isEmpty(l);
	}
	
	public void clear() {
		list.clear();
	}

}
