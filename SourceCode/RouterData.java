//Madan Bhurtel
//1001752499

package app;

/*
 * when route's link cost change, process class.
 */
public class RouterData implements Cloneable {
  int sourceid;       /* id of sending router sending this Data */
  int destid;         // id of router to which RouterData being sent 
  int[] cost = new int[RouterSimulator.NUM_NODES];    /* min cost to node 0 ... 6 */


  RouterData (int sourceID, int destID, int[] mincosts){
    this.sourceid = sourceID;
    this.destid = destID;
    System.arraycopy(mincosts, 0, this.cost, 0, RouterSimulator.NUM_NODES);
  }

  public Object clone(){
    try {
      RouterData newData = (RouterData) super.clone();
      newData.cost = (int[]) newData.cost.clone();
      return newData;
    }
    catch(Exception e){
      System.err.println(e);
      System.exit(1);
    }
    return null;
  }
    
}

