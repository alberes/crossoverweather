package com.crossover.trial.weather;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A collected point, including some information about the range of collected values
 *
 * @author code test administrator
 */
public class DataPoint implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -1773098160144967623L;

	public double mean = 0.0;

    public int first = 0;

    public int second = 0;

    public int third = 0;

    public int count = 0;

    /** private constructor, use the builder to create this object */
    private DataPoint() { }

    protected DataPoint(int first, int second, int mean, int third, int count) {
        this.setFirst(first);
        this.setMean(mean);
        this.setSecond(second);
        this.setThird(third);
        this.setCount(count);
    }

    /** the mean of the observations */
    public double getMean() {
        return mean;
    }

    protected void setMean(double mean) { this.mean = mean; }

    /** 1st quartile -- useful as a lower bound */
    public int getFirst() {
        return first;
    }

    protected void setFirst(int first) {
        this.first = first;
    }

    /** 2nd quartile -- median value */
    public int getSecond() {
        return second;
    }

    protected void setSecond(int second) {
        this.second = second;
    }

    /** 3rd quartile value -- less noisy upper value */
    public int getThird() {
        return third;
    }

    protected void setThird(int third) {
        this.third = third;
    }

    /** the total number of measurements */
    public int getCount() {
        return count;
    }

    protected void setCount(int count) {
        this.count = count;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    public boolean equals(Object that) {
        return this.toString().equals(that.toString());
    }

    static public class Builder {
        int first;
        int mean;
        int median;
        int last;
        int count;

        public Builder() { }

        public Builder withFirst(int first) {
            first= first;
            return this;
        }

        public Builder withMean(int mean) {
            mean = mean;
            return this;
        }

        public Builder withMedian(int median) {
            median = median;
            return this;
        }

        public Builder withCount(int count) {
            count = count;
            return this;
        }

        public Builder withLast(int last) {
            last = last;
            return this;
        }

        public DataPoint build() {
            return new DataPoint(this.first, this.mean, this.median, this.last, this.count);
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + count;
			result = prime * result + first;
			result = prime * result + last;
			result = prime * result + mean;
			result = prime * result + median;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Builder other = (Builder) obj;
			if (count != other.count)
				return false;
			if (first != other.first)
				return false;
			if (last != other.last)
				return false;
			if (mean != other.mean)
				return false;
			if (median != other.median)
				return false;
			return true;
		}
        
    }
}
