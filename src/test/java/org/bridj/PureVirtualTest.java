/* ============================================================================
 * Copyright (c) 2017 Imagic Bildverarbeitung AG, CH-8152 Glattbrugg.
 * All rights reserved.
 *
 * http://www.imagic.ch/
 * ============================================================================
 */
package org.bridj;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.bridj.ann.Library;
import org.bridj.ann.Name;
import org.bridj.ann.Namespace;
import org.bridj.ann.Ptr;
import org.bridj.ann.Virtual;
import org.bridj.cpp.CPPObject;
import org.bridj.cpp.CPPRuntime;
import org.bridj.util.Utils;
import org.junit.Test;

/**
 * Tests sub-classing pure virtual classes to implement callback interfaces.
 *
 * @version $Revision$
 * @version $Date$
 * @author $Author$
 * @owner Jens Hemprich
 */
public class PureVirtualTest {

    /**
     * A test class - there's no native implementation, we're just going to parse the structure into a vtable
     */
    @Namespace("test")
    @Library("TestLibrary")
    public static class TestClass extends CPPObject {
        static {
            BridJ.register();
        }

        public TestClass() {
            super();
        }

        public TestClass(Pointer pointer) {
            super(pointer);
        }

        @Name("stableEntry")
        @Virtual(0)
        public int stableEntry() {
            return (int) stableEntry$2();
        }

        @Name("stableEntry")
        @Ptr
        @Virtual(0)
        protected native long stableEntry$2();

        /**
         * Original signature : <code>Image* allocImageBuffer(ImageFormat*)</code><br>
         * <i>native declaration : line 36</i>
         */
        @Name("unstableEntry")
        @Virtual(1)
        public Pointer<?> unstableEntry(Pointer<?> pVoid) {
            return Pointer.pointerToAddress(unstableEntry(Pointer.getPeer(pVoid)), Void.class);
        }

        @Name("unstableEntry")
        @Ptr
        @Virtual(1)
        protected native long unstableEntry(@Ptr long pVoid);
    }

    public abstract static class TestableCPPRuntime extends CPPRuntime {
        abstract void test() throws Exception;
    }

    @Test
    public void testListVirtualMethods() throws Exception {
        TestableCPPRuntime testable = new TestableCPPRuntime() {
            @Override
            public void test() {
                List<VirtMeth> methods = new ArrayList<VirtMeth>();
                listVirtualMethods(Utils.getClass(TestClass.class), methods);

                assertEquals(2, methods.size());

                int nonNativeOverrides = 0;
                for (VirtMeth virtMeth : methods) {
                    if (!Modifier.isNative(virtMeth.implementation.getModifiers())) {
                        nonNativeOverrides++;
                    }
                }

                assertEquals(methods.size(), nonNativeOverrides);
            }
        };

        testable.test();
    }

    @Test
    public void testSysnthesizeVTableSize() throws Exception{
        TestableCPPRuntime testable = new TestableCPPRuntime() {
            @Override
            public void test() throws IOException {
                List<VirtMeth> methods = new ArrayList<VirtMeth>();
                listVirtualMethods(Utils.getClass(TestClass.class), methods);

                NativeLibrary nullableLibrary = null;
                CPPRuntime.VTable vtable = synthetizeVirtualTable(TestClass.class, null, methods, nullableLibrary); 
                
                assertEquals(methods.size(), vtable.callbacks.size());
            }
        };

        testable.test();
    }

    @Test
    public void testSysnthesizeVTableEntries() throws Exception{
        TestableCPPRuntime testable = new TestableCPPRuntime() {
            @Override
            public void test() throws IOException {
                List<VirtMeth> methods = new ArrayList<VirtMeth>();
                listVirtualMethods(Utils.getClass(TestClass.class), methods);

                NativeLibrary nullableLibrary = null;
                CPPRuntime.VTable vtable = synthetizeVirtualTable(TestClass.class, null, methods, nullableLibrary); 

                for (int i = 0; i < methods.size(); i++) {
                    assertNotNull("VTable pointer not initialized", vtable.ptr.get(i));
                }
                assertEquals(methods.size(), vtable.callbacks.size());
            }
        };

        testable.test();
    }
}
