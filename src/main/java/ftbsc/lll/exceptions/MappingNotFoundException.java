package ftbsc.lll.exceptions;

import ftbsc.lll.tools.SrgMapper;

/**
 * Thrown upon failure to find the requested mapping within a loaded {@link SrgMapper}.
 */
public class MappingNotFoundException extends RuntimeException {
	public MappingNotFoundException(String mapping) {
		super("Could not find mapping for " + mapping + "!");
	}
}
