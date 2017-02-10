package org.hisrc.stopdirect.jsi.rtree;

import org.junit.Assert;
import org.junit.Test;

import gnu.trove.procedure.TIntProcedure;
import net.sf.jsi.Point;
import net.sf.jsi.Rectangle;
import net.sf.jsi.SpatialIndex;
import net.sf.jsi.rtree.RTree;

public class RTreeTest {

	private SpatialIndex spatialIndex = new RTree();
	{
		spatialIndex.init(null);
	}
	
	@Test
	public void findsNearest(){
		spatialIndex.add(new Rectangle(10, 10, 10, 10), 0);
		spatialIndex.add(new Rectangle(30, 30, 30, 30), 1);
		
		spatialIndex.nearest(new Point(21, 21), new TIntProcedure() {
			
			@Override
			public boolean execute(int value) {
				Assert.assertEquals(1, value);
				return true;
			}
		}, Float.MAX_VALUE);
	}
}
