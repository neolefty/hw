package org.neolefty.cs143.hybrid_images.img.boof;

import org.neolefty.cs143.hybrid_images.img.HasProcessorParams;
import org.neolefty.cs143.hybrid_images.ui.ProcessorParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** Utility for creating things with {@link org.neolefty.cs143.hybrid_images.ui.ProcessorParam}s. */
public class HasProcessorParamsBase implements HasProcessorParams {
    private List<ProcessorParam> params = Collections.emptyList();

    public void addParam(ProcessorParam param) {
        addParams(Collections.singleton(param));
    }

    public void addParams(Collection<ProcessorParam> add) {
        List<ProcessorParam> tmp = new ArrayList<>(params);
        tmp.addAll(add);
        params = Collections.unmodifiableList(tmp);
    }

    public void addParams(HasProcessorParams source) {
        addParams(source.getProcessorParams());
    }

    @Override
    public Collection<ProcessorParam> getProcessorParams() {
        return params;
    }

    public void addIfHasParams(Object function) {
        if (function instanceof HasProcessorParams)
            addParams((HasProcessorParams) function);
    }
}
