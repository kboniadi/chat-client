package data;

import databus.DataBus;

public abstract class AbstractDataType implements IDataType {
    private DataBus dataBus;

    public DataBus getDataBus() {
        return dataBus;
    }

    public void setDataBus(DataBus dataBus) {
        this.dataBus = dataBus;
    }
}
