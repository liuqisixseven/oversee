package com.chd.common.core.dict;

import java.util.List;

public interface IDictCache<T extends IDictItem> {

    List<T> getDictItems(String code);
}
