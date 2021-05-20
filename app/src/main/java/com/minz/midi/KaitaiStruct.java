/**
 * Copyright 2015-2017 Kaitai Project: MIT license
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.minz.midi;

import java.nio.ByteBuffer;

/**
 * Common base class for all structured generated by Kaitai Struct.
 * Stores stream object that this object was parsed from in {@link #_io}.
 */
public class KaitaiStruct {
    /**
     * Stream object that this KaitaiStruct-based structure was parsed from.
     */
    protected KaitaiStream _io;

    public KaitaiStruct(KaitaiStream _io) {
        this._io = _io;
    }

    public KaitaiStream _io() { return _io; }

    /**
     * KaitaiStruct object that supports reading from a supplied stream object.
     */
    public abstract static class ReadOnly extends KaitaiStruct {
        public ReadOnly(KaitaiStream _io) {
            super(_io);
        }
        public abstract void _read();
    }

    /**
     * KaitaiStruct object that supports both reading from a given stream
     * object, and writing to a pre-supplied stream object or to a
     * stream object given explicitly. This also defines a few useful
     * shortcut methods.
     */
    public abstract static class ReadWrite extends ReadOnly {
        public ReadWrite(KaitaiStream _io) {
            super(_io);
        }
        public abstract void _write();
        public abstract void _check();

        public void _write(KaitaiStream io) {
            this._io = io;
            _write();
        }

        /**
         * Serializes current state of this object into byte array. Internally,
         * creates a fresh growable KaitaiStream, writes object's state into
         * it, and then converts it into byte array.
         * @return serialized object state
         */
        public byte[] _toByteArray() {
            KaitaiStream oldIo = _io;
            _io = new ByteBufferKaitaiStream(100000);
            _write();
            byte[] r = _io.toByteArrayFlip();
            _io = oldIo;
            return r;
        }
    }
}
