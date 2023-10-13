package com.grimacegram.grimacegram.shared;

/**
 * Class defining different views for JSON serialization using @JsonView.
 *
 * Views.Base: Default view to be used in most contexts, revealing non-sensitive user data.
 * Views.Sensitive: Extended view revealing sensitive or more detailed user data.
 */
public class Views {
    /**
     * Basic view, which should include non-sensitive data fields.
     */
    public interface Base{}
    /**
     * Sensitive view, which should include sensitive or additional data fields.
     * Fields in this view will only be serialized when the Sensitive view is specifically used.
     */
    public interface Sensitive extends Base {}
}
