package lab1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class graphWtest2 {

    private Graph graph;

    // 不再使用固定的 TEST_TEXT，而是根据测试用例动态构建图

    // 辅助方法：从文本中加载图（与之前相同，但现在用于定制图）
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
            graph.addEdge(word1, word2);
        }
    }

    // @BeforeEach 暂时不再加载默认图，因为白盒测试需要为每个用例定制图
    // 如果你希望在某些测试中仍然使用复杂图，可以保留一个独立的 @BeforeEach 或其他 setup 方法
    // @BeforeEach
    // void setup() {
    //     graph = new Graph();
    //     // loadGraphFromText(TEST_TEXT); // 这行在白盒测试中可能不再需要默认执行
    // }

    // 由于白盒测试需要针对性地构建图，我们将图的构建逻辑放入每个测试方法中
    // 或者提供一个辅助方法来创建特定结构的图

    // --- 白盒测试用例 ---

    @Test
    @DisplayName("白盒测试：两个输入词都不在图中 (路径 A.1)")
    void test_BothWordsNotInGraph_WhiteBox() {
        graph = new Graph(); // 创建一个空图
        String result = graph.queryBridgeWords("nonexistent1", "nonexistent2");
        assertEquals("No nonexistent1 and nonexistent2 in the graph!", result);
    }

    @Test
    @DisplayName("白盒测试：第一个输入词不在图中 (路径 A.2)")
    void test_FirstWordNotInGraph_WhiteBox() {
        graph = new Graph();
        loadGraphFromText("the quick brown fox"); // 图中包含 "the"
        String result = graph.queryBridgeWords("nonexistent", "the");
        assertEquals("No nonexistent in the graph!", result);
    }

    @Test
    @DisplayName("白盒测试：第二个输入词不在图中 (路径 A.3)")
    void test_SecondWordNotInGraph_WhiteBox() {
        graph = new Graph();
        loadGraphFromText("the quick brown fox"); // 图中包含 "the"
        String result = graph.queryBridgeWords("the", "nonexistent");
        assertEquals("No nonexistent in the graph!", result);
    }

    @Test
    @DisplayName("白盒测试：所有词都在，但无桥接词 (路径 B & C.1)")
    void test_NoBridgeWordsFound_WhiteBox() {
        graph = new Graph();
        // 构建一个图，其中 "apple" -> "pie"，"chart" -> "report"，但 "apple" 到 "report" 没有桥接词
        loadGraphFromText("apple pie chart report");
        String result = graph.queryBridgeWords("apple", "report");
        assertEquals("No bridge words from apple to report!", result);
    }

    @Test
    @DisplayName("白盒测试：所有词都在，且只有一个桥接词 (路径 B & C.2)")
    void test_OneBridgeWordFound_WhiteBox() {
        graph = new Graph();
        // 构建图: "start -> middle", "middle -> end"
        loadGraphFromText("start middle end");
        String result = graph.queryBridgeWords("start", "end");
        assertEquals("The bridge word from start to end is: middle.", result);
    }

    @Test
    @DisplayName("白盒测试：所有词都在，且有多个桥接词 (路径 B & C.2)")
    void test_MultipleBridgeWordsFound_WhiteBox() {
        graph = new Graph();
        // 构建图: "start -> m1 -> end" 和 "start -> m2 -> end"
        loadGraphFromText("start middlea end and start middleb end");
        String result = graph.queryBridgeWords("start", "end");

        // 由于多个桥接词的顺序不确定，使用 assertTrue 检查是否包含所有预期的桥接词
        assertTrue(result.contains("middlea") && result.contains("middleb"), "应找到 'middleA' 和 'middleB' 作为桥接词");
        assertTrue(result.contains("The bridge words from start to end are:"), "结果应包含桥接词前缀");
    }

    @Test
    @DisplayName("白盒测试：w1 在图中，但没有出边 (路径 B & C.1)")
    void test_NoOutgoingEdgesFromWord1_WhiteBox() {
        graph = new Graph();
        graph.addEdge("isolated", "target"); // Ensure "isolated" is a node, but no other word points from "isolated"
        graph.addEdge("another", "word"); // Some other words
        // 移除 "isolated" 的出边，使其只有入边或孤立
        graph.adjacencyList.get("isolated").clear(); // 直接操作 internal state for white-box test.txt specific setup
        // Note: Direct access to `adjacencyList` might be needed for very specific white-box setups,
        // but generally prefer `addEdge` or other public methods if available.
        // If adjacencyList is private, you might need to use a temporary, controlled setup.

        String result = graph.queryBridgeWords("isolated", "target");
        assertEquals("No bridge words from isolated to target!", result);
    }

}