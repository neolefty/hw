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

    protected void addParam(ProcessorParam param) {
        addParams(Collections.singleton(param));
    }

    protected void addParams(Collection<ProcessorParam> add) {
        List<ProcessorParam> tmp = new ArrayList<>(params);
        tmp.addAll(add);
        params = Collections.unmodifiableList(tmp);
    }

    protected void addParams(HasProcessorParams source) {
        addParams(source.getProcessorParams());
    }

    @Override
    public Collection<ProcessorParam> getProcessorParams() {
        return params;
    }
}
