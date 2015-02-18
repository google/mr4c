Test Plan
=========

The objective of this test is to run a simple algorithm in Hadoop using MR4C against some input datasets in HDFS and output results into a file. We have provided a gold standard reference image, and the script will run a diff against your result to make sure that our algorithm worked as expected on the cluster. Success in this script indicates that hadoop, mr4c, hdfs, and all dependencies are all working together.

Execute the test with:

     ./test_mr4c.sh

Expected Successful output:

     SUCCESS: Hadoop MR4C job ran successfully.  You may verify the output by running the bin/check_mr4c_output tool
     Running diff against reference data
     SUCCESS: MR4CRef output successfully validated

Expected Failed output:

     ERROR: mapper failed.  Run bin/test_hadoop tool to ensure all services are running
     removing /tmp/mr4c_avgoutput
     Running diff against reference data
     diff: /tmp/mr4c_avgoutput/avg_pixels.csv: No such file or directory
     ERROR: MR4CRef not validated: ref/avg_pixels.csv /tmp/mr4c_avgoutput/avg_pixels.csv

If you have any trouble with this test please contact us at mr4c@googlegroups.com
