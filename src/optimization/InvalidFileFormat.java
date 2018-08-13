/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package optimization;

/**
 *
 * @author toshiba
 */
class InvalidFileFormat extends Exception {

    private String message = null;
    
    public InvalidFileFormat(String message) {
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    @Override
    public String toString() {
        return message;
    }
}
