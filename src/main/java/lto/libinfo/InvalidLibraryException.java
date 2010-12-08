package lto.libinfo;

public class InvalidLibraryException extends RuntimeException
{
    InvalidLibraryException(){super();}
    InvalidLibraryException(String message)
    {
        super(message);
    }
}
