/*
This is a generic implementation of a queue
This is implemented using an array. 
The items are inserted modulo the length of the array, 
so the end of the queue may wrap around to the front of the array
 */


class Queue<T>{
    private T queueArray[];
    private int length;
    private int start;
    private int end;
    private int numEntries;

    /*
      Constructor: 
      Takes one argument, length, that is the users intending length of the queue. 
      Initiates the queueArray to the length of the argument. 
     */
    public Queue(int length){
	queueArray = (T[]) new Object[length];
	this.length = length;
	start = 0;
	end = 0;
    }

    /*
      Takes one argument, value, which will be added to the queue. 
      It will place value at the end of the queue
      If the value is placed in the final position in the array, the end of the array 
      will be the first index of the array. 
      Prints a warning message and returns if the queue is full 
     */
    public void push(T value){
	if (numEntries == length){
	    System.out.println("The Queue is full");
	    return; 
	}
	queueArray[end] = value;
	end = (end + 1) % length;
	numEntries++;
    }
    /*
      Does not take any arguments 
      Returns the value of the start of the array
      Increases the start index of the queue
      If the previous index was in the final position in the array, the start of the array 
      will be the first index of the array. 
      Prints a warning message and returns null of the queue is empty. 
     */
    public T pop(){
	if (numEntries == 0){
	    System.out.println("The queue is empty");
	    return null;
	}
	T value = queueArray[start];
	start = (start + 1) % length;
	numEntries--;
	return value;
    }
    
}
