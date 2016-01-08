import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
   * Limps like arm, leg, and finger.They may contain different parts and different
   * joints. This difference will be reflect in the constructor method.
   * @author Zhi Dou
   * @since Fall 2011
   */
  public class Limbs 
  {
    /** The base part. */
    private final Component basepart;
    /** The middle part. */
    private final Component middlepart;
    /** The selective part. */
    private final Component selectivepart;
    /** The last part.*/
    private final Component lastpart;
    /** The list of all the joints in this finger. */
    private final List<Component> joints;

    /**
     * Instantiates this limbs with the three specified joints.
     * 
     * @param basepart
     *          The palm joint of this finger.
     * @param middlepart
     *          The middle joint of this finger.
     * @param lastpart
     *          The distal joint of this finger.
     * Since there are three parts of finger, the selective part is useless.
     */
    public Limbs(final Component basepart, final Component middlepart,
    		final Component selectivepart, final Component lastpart)
    {
    		this.basepart = basepart;
        this.middlepart = middlepart;
        this.selectivepart=selectivepart;
        this.lastpart = lastpart;
        if (selectivepart!=null)
        this.joints = Collections.unmodifiableList(Arrays.asList(this.basepart,
            this.middlepart, this.selectivepart, this.lastpart));
        else 
        	 	this.joints = Collections.unmodifiableList(Arrays.asList(this.basepart,
        	            this.middlepart, this.lastpart));
    }
    /**
     * Gets the base part.
     * 
     * @return The base part.
     */
    Component basepart() {
      return this.basepart;
    }
    

    /**
     * Gets an unmodifiable view of the list of the joints.
     * 
     * @return An unmodifiable view of the list of the joints.
     */
    List<Component> joints() {
      return this.joints;
    }

    /**
     * Gets the middle part.
     * 
     * @return The middle part.
     */
    Component middlepart() {
      return this.middlepart;
    }

    /**
     * Gets the selective part.
     * 
     * @return The palm joint selective part.
     */
    Component selectivepart() {
      return this.selectivepart;
    }
    Component lastpart() {
        return this.lastpart;
      }
  }