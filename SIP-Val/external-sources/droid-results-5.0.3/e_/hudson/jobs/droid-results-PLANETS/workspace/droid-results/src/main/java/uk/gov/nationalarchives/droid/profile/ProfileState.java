/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.profile;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author rflitcroft
 *
 */
public enum ProfileState {
    
    /** @return The profile is running. */
    RUNNING(true) { @Override 
        ProfileState[] nextStates() {
            return new ProfileState[] {STOPPED, FINISHED};
        }
    },
    
    /** @return The profile has been stopped. */
    STOPPED(false) { @Override ProfileState[] nextStates() {
            return new ProfileState[] {RUNNING, SAVING, FINISHED};
        }
    },
    
    /** @return The profile is saving to disk. */
    SAVING(true) { @Override ProfileState[] nextStates() {
            return new ProfileState[] {VIRGIN, STOPPED, FINISHED};
        }
    },
    
    /** @return The profile is loading from disk. */
    LOADING(true) { @Override ProfileState[] nextStates() {
            return new ProfileState[] {VIRGIN, STOPPED, FINISHED};
        }
    },
    
    /** @return The profile is being initialised. */
    INITIALISING(true) { @Override ProfileState[] nextStates() {
            return new ProfileState[] {VIRGIN, STOPPED, FINISHED};
        }
    }, 
    
    /** @return The profile has never been run. */
    VIRGIN(false) { @Override ProfileState[] nextStates() {
            return new ProfileState[] {RUNNING, SAVING};
        }
    },

    /** @return The profile has finished. */
    FINISHED(false) { @Override ProfileState[] nextStates() {
            return new ProfileState[] {SAVING};
        }
    }; 

    private boolean isTransient;
    
    private ProfileState(boolean isTransient) {
        this.isTransient = isTransient;
    }
    
    /**
     * @return the valid next states for the current state.
     */
    abstract ProfileState[] nextStates();
    
    /**
     * @return the valid next states for the current state.
     */
    public Collection<ProfileState> allowedNextStates() {
        return Arrays.asList(nextStates());
    }
    
    /**
     * @return True if this is a transient state, false otherwise.
     * @return
     */
    public boolean isTransient() {
        return isTransient;
    }
    
}
