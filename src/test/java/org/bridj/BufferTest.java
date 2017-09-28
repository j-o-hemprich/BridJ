package org.bridj;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.bridj.Pointer;

/**
 * Test buffer access.
 *
 * @version $Revision$
 * @version $Date$
 * @author $Author$
 * @owner Jens Hemprich
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BufferTest {
    @Test
    public void test1_BridJPointerToBufferArray() {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        assertNotNull(Pointer.pointerToBytes(buffer));
    }

    /**
     * This test failed badly prior to the corresponding bugfix when executed as the first testcase.
     * Once BridJ has been properly initialized, the test will succeed - therfore it passes the automated tests 
     * even if you revert the fix.
     */
    @Test
    public void test2_BridJPointerToDirectBuffer() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(8);
        assertNotNull(Pointer.pointerToBytes(buffer));
    }
}
