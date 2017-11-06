package com.github.piasy.audio_mixer;

/**
 * Created by Piasy{github.com/Piasy} on 05/11/2017.
 */

public class AudioResampler {

    private long mNativeHandle;
    private BufferInfo mInputBuffer;
    private BufferInfo mOutputBuffer;

    public AudioResampler(int inChannelNum, int inSampleRate, int outChannelNum, int outSampleRate,
            int msPerBuf) {
        int inSamplesPerBuf = inSampleRate / (1000 / msPerBuf);

        mNativeHandle = nativeInit(inChannelNum, inSampleRate, inSamplesPerBuf, outChannelNum,
                outSampleRate);

        int inputBufferSize = inSampleRate / (1000 / msPerBuf) * inChannelNum * 2;
        mInputBuffer = new BufferInfo(new byte[inputBufferSize], inputBufferSize);

        int outputBufferSize = outSampleRate / (1000 / msPerBuf) * outChannelNum * 2;
        // there may have some delay in swr, so output buffer may be lager
        outputBufferSize *= 2;
        mOutputBuffer = new BufferInfo(new byte[outputBufferSize], 0);
    }

    private static native long nativeInit(int inChannelNum, int inSampleRate, int inSamples,
            int outChannelNum, int outSampleRate);

    private static native int nativeResample(long handle, byte[] inData, int inLen, byte[] outData);

    private static native void nativeDestroy(long handle);

    public BufferInfo getInputBuffer() {
        return mInputBuffer;
    }

    public BufferInfo resample(BufferInfo inputBuffer) {
        mOutputBuffer.size = nativeResample(mNativeHandle, inputBuffer.buffer, inputBuffer.size,
                mOutputBuffer.buffer);
        return mOutputBuffer;
    }

    public void destroy() {
        nativeDestroy(mNativeHandle);
    }

    public static class BufferInfo {
        private byte[] buffer;
        private int size;

        private BufferInfo(final byte[] buffer, final int size) {
            this.buffer = buffer;
            this.size = size;
        }

        public byte[] getBuffer() {
            return buffer;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }
}
