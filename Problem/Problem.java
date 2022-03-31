//I'm going to start with  the WordCount2 example as a Base to solve this problem, as it already has a system to read multiple files 
//and  find the  most common  words in all of them; all I need to do is arrange them  according to value instead of key!

//We start by taking the 20 data files and running them through WordCount2. 

import java.io.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Problem {  
	public static class Rev_Int_Map extends Mapper<Object, Text, IntWritable, Text> {
	  
		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String[] KVpair = value.toString().split("\\s+");
			String word = KVpair[0];
			int frequency = -1 * Integer.parseInt(KVpair[1]); 
			context.write(new IntWritable(frequency), new Text(word));
		}
	}
	  
	public static class Rev_Int_Reduce extends Reducer<IntWritable, Text, IntWritable, Text> {
	  
		static int list_num;
		private IntWritable Order = new IntWritable();
	  
		@Override
		public void setup(Context context) throws IOException, InterruptedException {
			list_num = 1;
		}
	  
		@Override
		public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			String word = null;
			for (Text val : values) {
				word = val.toString();
			}
			if (!(list_num > 100)) {
				Order.set(key.get());
				context.write(Order, new Text(word));
				list_num++;
			}
		}
	}
  

//This section is identical to WordCount2.java except we do not need a combiner and we use only 2 arguments here: input file directory in HDFS and output too.
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        GenericOptionsParser optionParser = new GenericOptionsParser(conf, args);
		String[] remainingArgs = optionParser.getRemainingArgs();
		if ((remainingArgs.length < 2) || (remainingArgs.length > 2)) {
		  System.err.println("Usage: problem <in> <out> ");
		  System.exit(2);
		}
  
        Job job = new Job(conf, "problem");
		job.setJar("Problem.jar");
  
        job.setMapperClass(Rev_Int_Map.class);
        job.setReducerClass(Rev_Int_Reduce.class);
        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(remainingArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(remainingArgs[1]));
  
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}