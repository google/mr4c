# Using YARN Resource Allocation with MR4C

The previous examples illustrate the fundamental interfaces that MR4C uses to connect algorithms to datasets, but you may notice that we have only worked on the local machine using the mr4c executable. This example will show you the true power of MR4C by executing the algorithms in Hadoop using the mr4c_hadoop executable.

Additionally, we introduce a simple map reduce workflow including two steps that work in conjunction to allow you to split the work accross into many processes, and reduce the results from each of those processes into a single answer. While we do that we will also introduce the MR4C/YARN features using dynamic resource allocation. We use two algorithms configured with map.json and reduce.json. The resources are allocated in mapReduce.sh using the following parameters:

    -R
    -Hcluster
    -Htasks
    -Hcores.min
    -Hcores.max
    -Hmemory.min
    -Hmemory.max

For example:

mr4c_hadoop exe.json -R=runtime.properties -Hcluster=cdh5 -Htasks=5 -Hcores.min=1 -Hcores.max=10 -Hmemory.min=1024 -Hmemory.max=16384

The cluster will be queried for its resource limits:
* If max is provided for a resource, mr4c will ask for the max, subject to the limit.
* If min is provided, mr4c will validate that it is less than the limit.
* If min is provided with no max, then mr4c will ask for the min value.
* No max or min means no resource request


The Hcluster names can be configured in the $MR4C_HOME/bin/java_yarn/conf/site.json file. Alternatively, you can assign a site.json using the $MR4C_SITE variable.

## MapReduce
The two algorithms in this example illustrate basic mapper and reducer steps:
* Mapper: collect individual histograms for the pixel values from a series of input images in a map step.
* Reducer: combine all the histograms into a single histogram for all input images.

In this example, we set the resources explicitly in the mapReduce.sh file for each algorithm.
