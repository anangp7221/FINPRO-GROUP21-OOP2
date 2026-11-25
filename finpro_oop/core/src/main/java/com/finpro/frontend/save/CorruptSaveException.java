package com.finpro.frontend.save;

/** File save ada tapi isinya rusak / tidak valid / sudah diubah. */
public class CorruptSaveException extends SaveException {
    public CorruptSaveException(String message) { super(message); }
    public CorruptSaveException(String message, Throwable cause) { super(message, cause); }
}
