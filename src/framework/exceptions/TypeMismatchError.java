package framework.exceptions;

public class TypeMismatchError extends Error
{
    public TypeMismatchError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TypeMismatchError(String message, Throwable cause)
    {
        super(message, cause);
    }

    public TypeMismatchError(Throwable cause)
    {
        super(cause);
    }

    public TypeMismatchError()
    {
        super();
    }

    public TypeMismatchError(String message)
    {
        super(message);
    }
}
