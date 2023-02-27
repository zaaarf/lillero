package ftbsc.lll.exceptions;

/**
 * Thrown upon failure to find the requested mapping within a loaded SrgMapper.
 */
public class MappingNotFoundException extends RuntimeException {
	public MappingNotFoundException(String mapping) {
		super("Could not find mapping for " + mapping + "!");
	}
}
