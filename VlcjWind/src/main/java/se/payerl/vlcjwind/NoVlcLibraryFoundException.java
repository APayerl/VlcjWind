package se.payerl.vlcjwind;

@SuppressWarnings("serial")
public class NoVlcLibraryFoundException extends RuntimeException{
	public NoVlcLibraryFoundException(String message) {
		super(message);
	}
}