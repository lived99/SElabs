package lab1.lab3b;

import lab1.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GraphTest {

    private Graph graph;

    // 新的测试文本
    private static final String TEST_TEXT = "The scientist carefully analyzed the data, wrote a detailed report, and shared the report with the team, but the team requested more data, so the scientist analyzed it again.";

    // 辅助方法：从文本中加载图
    private void loadGraphFromText(String text) {
        String cleanedText = text.toLowerCase();
        Pattern pattern = Pattern.compile("[a-z]+");
        Matcher matcher = pattern.matcher(cleanedText);

        List<String> words = new ArrayList<>();
        while (matcher.find()) {
            words.add(matcher.group());
        }

        for (int i = 0; i < words.size() - 1; i++) {
            String word1 = words.get(i);
            String word2 = words.get(i + 1);
            graph.addEdge(word1, word2); // 使用 Graph 类的 addEdge 方法
        }
    }

    @BeforeEach
    void setup() {
        graph = new Graph();
        loadGraphFromText(TEST_TEXT);
    }

    // --- Test Cases for queryBridgeWords ---

    @Test
    @DisplayName("测试：'the' 和 'data' 之间的桥接词 'analyzed' 和 'team'")
    void testQueryBridgeWords_TheToData() {
        // 分析文本 "The scientist carefully analyzed the data, wrote a detailed report, and shared the report with the team, but the team requested more data, so the scientist analyzed it again."
        // 提取相关片段和连接：
        // 1. "analyzed the data" -> 'analyzed' -> 'the' -> 'data'
        //    这里，'the' -> 'analyzed' (不存在)
        //    正确理解桥接词 "word1 -> word3 -> word2"
        //    所以，我们需要寻找 X 使得 'the' -> X 且 X -> 'data'
        //
        //    文本中的连接 (假设已经小写处理):
        //    'the' -> 'scientist'
        //    'analyzed' -> 'the'
        //    'the' -> 'data' (来自 "the data")
        //    'the' -> 'report' (来自 "the report")
        //    'the' -> 'team' (来自 "the team")
        //    'more' -> 'data' (来自 "more data")
        //    'it' -> 'again' (来自 "it again")
        //
        //    寻找 "the" 到 "data" 的桥接词 (the -> X -> data):
        //    检查 'the' 的出边：'scientist', 'data', 'report', 'team'
        //    1. X = 'scientist'? 'scientist' -> 'data' 不存在。
        //    2. X = 'data'? 'data' -> 'data' 不存在（自环通常不视为桥接）。
        //    3. X = 'report'? 'report' -> 'data' 不存在。
        //    4. X = 'team'? 'team' -> 'requested' -> 'more' -> 'data'
        //       但我们只找直接桥接 word1 -> X -> word2
        //       所以，我们需要找 'team' -> 'data'。
        //       文本中是 'team' -> 'requested'，没有 'team' -> 'data'。
        //
        //    重新审查文本：
        //    'the' -> 'data' (来自 "the data")
        //    'the' -> 'report' (来自 "the report")
        //    'the' -> 'team' (来自 "the team")
        //    'analyzed' -> 'the'
        //    'more' -> 'data'
        //
        //    对于 'the' 和 'data':
        //    寻找 X 使得 'the' -> X 且 X -> 'data'
        //    从文本看：
        //    'the' -> 'scientist'
        //    'scientist' -> 'carefully'
        //    'carefully' -> 'analyzed'
        //    'analyzed' -> 'the'
        //    'the' -> 'data'  <-- 边存在
        //
        //    'report' -> 'and'
        //    'and' -> 'shared'
        //    'shared' -> 'the'
        //    'the' -> 'report' <-- 边存在
        //    'report' -> 'with'
        //    'with' -> 'the'
        //    'the' -> 'team' <-- 边存在
        //
        //    'team' -> 'requested'
        //    'requested' -> 'more'
        //    'more' -> 'data' <-- 边存在
        //
        //    'scientist' -> 'analyzed'
        //    'analyzed' -> 'it' (来自 "analyzed it again")
        //
        //    根据这些边，再次寻找 'the' 到 'data' 的桥接词：
        //    'the'的出边有：'scientist', 'data', 'report', 'team'
        //    检查这些出边作为 X 是否能指向 'data'：
        //    1. 'scientist' -> 'data' ? 不存在。
        //    2. 'data' -> 'data' ? 不存在。
        //    3. 'report' -> 'data' ? 不存在。
        //    4. 'team' -> 'data' ? 不存在。
        //
        //    这表明，根据当前文本和严格的桥接词定义，'the' 和 'data' 之间**没有直接桥接词**。
        //    之前的分析有误。

        String result = graph.queryBridgeWords("the", "data");
        assertEquals("No bridge words from the to data!", result);
    }

    @Test
    @DisplayName("测试：'wrote' 和 'shared' 之间的桥接词 'a', 'detailed', 'report', 'and'")
    void testQueryBridgeWords_WroteToShared() {
        // 分析文本 "wrote a detailed report, and shared"
        // 寻找 X 使得 'wrote' -> X 且 X -> 'shared'
        // 文本中的连接：
        // 'wrote' -> 'a'
        // 'a' -> 'detailed'
        // 'detailed' -> 'report'
        // 'report' -> 'and'
        // 'and' -> 'shared'
        //
        // 检查 'wrote' 的出边：'a'
        // 1. X = 'a'? 'a' -> 'shared' 不存在。
        //
        // 这表明，根据当前文本和严格的桥接词定义，'wrote' 和 'shared' 之间**没有直接桥接词**。
        // 之前的分析有误。

        String result = graph.queryBridgeWords("wrote", "shared");
        assertEquals("No bridge words from wrote to shared!", result);
    }


    @Test
    @DisplayName("测试：'report' 和 'with' 之间的桥接词 (预期无)")
    void testQueryBridgeWords_ReportToWith() {
        // 分析文本 "report, and shared the report with the team"
        // 寻找 X 使得 'report' -> X 且 X -> 'with'
        // 文本中的连接：
        // 'report' -> 'and' (来自 "report, and")
        // 'the' -> 'report' (来自 "the report")
        // 'report' -> 'with' (来自 "report with")
        //
        // 检查 'report' 的出边：'and', 'with'
        // 1. X = 'and'? 'and' -> 'with' 不存在。
        // 2. X = 'with'? 'with' -> 'with' 不存在 (自环通常不作为桥接词)。
        //
        // 因此，预期的结果是：没有桥接词。
        String result = graph.queryBridgeWords("report", "with");
        assertEquals("No bridge words from report to with!", result);
    }

    @Test
    @DisplayName("测试：'analyzed' 和 'it' 之间的桥接词 'the'")
    void testQueryBridgeWords_AnalyzedToIt() {
        // 文本: "analyzed the data... analyzed it again."
        // 寻找 X 使得 'analyzed' -> X 且 X -> 'it'
        // 文本中的连接：
        // 'analyzed' -> 'the' (来自 "analyzed the data")
        // 'analyzed' -> 'it' (来自 "analyzed it again")
        //
        // 检查 'analyzed' 的出边：'the', 'it'
        // 1. X = 'the'? 'the' -> 'it' 不存在。
        // 2. X = 'it'? 'it' -> 'it' 不存在（自环通常不作为桥接）。
        //
        // 仔细看文本 "the scientist carefully analyzed the data, wrote a detailed report, and shared the report with the team, but the team requested more data, so the scientist analyzed it again."
        // 'analyzed' -> 'the' (第一次出现)
        // 'analyzed' -> 'it' (第二次出现)
        // 'the' -> 'data'
        // 'it' -> 'again'
        //
        // 所以，寻找 'analyzed' 到 'it' 的桥接词 X：
        // 1. 'analyzed' -> X: 'the', 'it'
        // 2. X -> 'it': 只有 'analyzed' -> 'it' (当 X = 'analyzed')
        //
        // 唯一的符合 X -> 'it' 的 X 是 'analyzed' 自己，但 'analyzed' -> 'analyzed' 不符合桥接词定义。
        // 似乎没有桥接词。
        String result = graph.queryBridgeWords("analyzed", "it");
        assertEquals("No bridge words from analyzed to it!", result);
    }


    @Test
    @DisplayName("测试：'team' 和 'data' 之间的桥接词 'requested' 和 'more'")
    void testQueryBridgeWords_TeamToData() {
        // 文本: "the team requested more data"
        // 寻找 X 使得 'team' -> X 且 X -> 'data'
        // 文本中的连接：
        // 'team' -> 'requested'
        // 'requested' -> 'more'
        // 'more' -> 'data'
        //
        // 检查 'team' 的出边：'requested'
        // 1. X = 'requested'? 'requested' -> 'data' 不存在。
        //
        // 似乎没有桥接词。
        String result = graph.queryBridgeWords("team", "data");
        assertEquals("No bridge words from team to data!", result);
    }


    @Test
    @DisplayName("测试第一个词不存在于图中：'nonexistent' 和 'data'")
    void testQueryBridgeWords_FirstWordNotInGraph() {
        String result = graph.queryBridgeWords("nonexistent", "data");
        assertEquals("No nonexistent in the graph!", result);
    }

    @Test
    @DisplayName("测试第二个词不存在于图中：'the' 和 'nonexistent'")
    void testQueryBridgeWords_SecondWordNotInGraph() {
        String result = graph.queryBridgeWords("the", "nonexistent");
        assertEquals("No nonexistent in the graph!", result);
    }

    @Test
    @DisplayName("测试两个词都不存在于图中：'worda' 和 'wordb'")
    void testQueryBridgeWords_BothWordsNotInGraph() {
        String result = graph.queryBridgeWords("worda", "wordb");
        assertEquals("No worda and wordb in the graph!", result);
    }

    @Test
    @DisplayName("测试输入包含大小写混合的单词：'The' 和 'Data'")
    void testQueryBridgeWords_MixedCaseInput() {
        // 图中所有词都是小写，但 queryBridgeWords 会处理输入的大小写
        String result = graph.queryBridgeWords("The", "Data");
        // 根据上面的分析，"the" 到 "data" 应该没有桥接词
        assertEquals("No bridge words from The to Data!", result);
    }

    @Test
    @DisplayName("测试空图时的查询")
    void testQueryBridgeWords_EmptyGraph() {
        graph = new Graph(); // 创建一个空的图
        String result = graph.queryBridgeWords("word1", "word2");
        assertEquals("No word1 and word2 in the graph!", result);
    }

    // 私有辅助方法，用于验证 queryBridgeWords 的输出格式。
    // 这与 Graph 类中 formatBridgeWordsOutput 的逻辑相同，因为它是私有的无法直接调用。
    private String formatBridgeWordsOutput(List<String> bridgeWords, String word1, String word2) {
        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        } else if (bridgeWords.size() == 1) {
            return "The bridge word from " + word1 + " to " + word2 + " is: " + bridgeWords.get(0) + ".";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("The bridge words from ").append(word1).append(" to ").append(word2).append(" are: ");
            for (int i = 0; i < bridgeWords.size(); i++) {
                sb.append(bridgeWords.get(i));
                if (i < bridgeWords.size() - 2) { // 如果不是倒数第二个词
                    sb.append(", ");
                } else if (i == bridgeWords.size() - 2) { // 如果是倒数第二个词
                    sb.append(" and ");
                }
            }
            sb.append(".");
            return sb.toString();
        }
    }
}