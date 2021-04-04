package databus;

import data.IDataType;

public interface Member {
    void send(IDataType event);
}
