package com.org.strom;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

 
public class IgnoreWordsBolt extends BaseRichBolt {
	
	private static final long serialVersionUID = 6069146554651714100L;
	
	private Set<String> IGNORE_LIST = new HashSet<String>(Arrays.asList(new String[] {
			"car","auto","auto-mobile","auto mobile", "four wheeler"
            
    }));
    private OutputCollector collector;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
    	
    
        String lang = (String) input.getValueByField("lang");
        String word = (String) input.getValueByField("word");
        String geo = (String) input.getValueByField("geo");
        Boolean geo_enabled = (Boolean) input.getValueByField("geo_enabled");
       // if (!IGNORE_LIST.contains(word)) {
            collector.emit(new Values(lang, word,geo,geo_enabled));
       //  }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("lang", "word","geo","geo_enabled"));
    }
}
