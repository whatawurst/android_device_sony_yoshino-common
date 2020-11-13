package com.android.setupwizardlib.items;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.InflateException;
import java.lang.reflect.Constructor;
import java.util.HashMap;

public abstract class ReflectionInflater<T> extends SimpleInflater<T> {
    private static final Class<?>[] CONSTRUCTOR_SIGNATURE = new Class[]{Context.class, AttributeSet.class};
    private static final HashMap<String, Constructor<?>> sConstructorMap = new HashMap();
    @NonNull
    private final Context mContext;
    @Nullable
    private String mDefaultPackage;
    private final Object[] mTempConstructorArgs = new Object[2];

    protected ReflectionInflater(@NonNull Context context) {
        super(context.getResources());
        this.mContext = context;
    }

    @NonNull
    public final T createItem(String str, String str2, AttributeSet attributeSet) {
        Object concat;
        if (str2 == null || str.indexOf(46) != -1) {
            String concat2 = str;
        } else {
            concat2 = str2.concat(str);
        }
        Constructor constructor = (Constructor) sConstructorMap.get(concat2);
        if (constructor == null) {
            try {
                constructor = this.mContext.getClassLoader().loadClass(concat2).getConstructor(CONSTRUCTOR_SIGNATURE);
                constructor.setAccessible(true);
                sConstructorMap.put(str, constructor);
            } catch (Exception e) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(attributeSet.getPositionDescription());
                stringBuilder.append(": Error inflating class ");
                stringBuilder.append(concat2);
                throw new InflateException(stringBuilder.toString(), e);
            }
        }
        this.mTempConstructorArgs[0] = this.mContext;
        this.mTempConstructorArgs[1] = attributeSet;
        Object newInstance = constructor.newInstance(this.mTempConstructorArgs);
        this.mTempConstructorArgs[0] = null;
        this.mTempConstructorArgs[1] = null;
        return newInstance;
    }

    @NonNull
    public Context getContext() {
        return this.mContext;
    }

    @Nullable
    public String getDefaultPackage() {
        return this.mDefaultPackage;
    }

    /* Access modifiers changed, original: protected */
    public T onCreateItem(String str, AttributeSet attributeSet) {
        return createItem(str, this.mDefaultPackage, attributeSet);
    }

    public void setDefaultPackage(@Nullable String str) {
        this.mDefaultPackage = str;
    }
}
