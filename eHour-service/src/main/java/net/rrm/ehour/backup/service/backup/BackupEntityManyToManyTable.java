package net.rrm.ehour.backup.service.backup;

import net.rrm.ehour.domain.DomainObject;
import net.rrm.ehour.persistence.backup.dao.BackupRowProcessor;

import java.io.Serializable;

public class BackupEntityManyToManyTable implements BackupEntity {
    private String parentName;
    private Class<? extends DomainObject<? extends Serializable, ?>> domainObjectClass;
    private BackupRowProcessor processor;
    private String name;
    private int order;

    public BackupEntityManyToManyTable(String name, int order) {
        this(null, name, name + "S", order);
    }

    public BackupEntityManyToManyTable(Class<? extends DomainObject<? extends Serializable, ?>> domainObjectClass, String name, int order) {
        this(domainObjectClass, name, name + "S", order);
    }

    public BackupEntityManyToManyTable(Class<? extends DomainObject<? extends Serializable, ?>> domainObjectClass, String name, String parentName, int order) {
        this(domainObjectClass, name, parentName, null, order);
    }

    public BackupEntityManyToManyTable(Class<? extends DomainObject<? extends Serializable, ?>> domainObjectClass, String name, String parentName, BackupRowProcessor processor, int order) {
        this.domainObjectClass = domainObjectClass;
        this.name = name;
        this.parentName = parentName;
        this.processor = processor;
        this.order = order;
    }

    public String getParentName() {
        return parentName;
    }

    public Class<? extends DomainObject<? extends Serializable, ?>> getDomainObjectClass() {
        return domainObjectClass;
    }

    public BackupRowProcessor getProcessor() {
        return processor;
    }

    public String name() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BackupEntity that = (BackupEntity) o;

        if (domainObjectClass != null ? !domainObjectClass.equals(that.getDomainObjectClass()) : that.getDomainObjectClass() != null)
            return false;
        if (name != null ? !name.equals(that.name()) : that.name() != null) return false;
        if (parentName != null ? !parentName.equals(that.getParentName()) : that.getParentName() != null) return false;
        if (processor != null ? !processor.equals(that.getProcessor()) : that.getProcessor() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = parentName != null ? parentName.hashCode() : 0;
        result = 31 * result + (domainObjectClass != null ? domainObjectClass.hashCode() : 0);
        result = 31 * result + (processor != null ? processor.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }


    @Override
    public int compareTo(BackupEntity o) {
        return o.getOrder() - order;
    }
}
