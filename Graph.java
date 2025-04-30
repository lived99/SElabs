//修改4：ide管理

package lab1;

//修改2 S2R5
// Graph.java
import java.io.*;
import java.util.*;

//修改3 S2R7 B2
public class Graph {
    private Map<String, Map<String, Integer>> adjacencyList;
    private Map<String, Integer> distances;
    private Map<String, List<String>> predecessors;
    private Map<String, Double> pageRank;
    private Map<String, Integer> wordFrequency;
    private Map<String, Double> outWeights;
    private Map<String, Set<String>> reverseAdjacencyList;

    public Graph() {
        adjacencyList = new HashMap<>();
    }

    private boolean isPathEdge(String source, String target, List<String> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            if (path.get(i).equals(source) && path.get(i + 1).equals(target)) {
                return true;
            }
        }
        return false;
    }

    private static class Node {
        String word;
        int distance;

        public Node(String word, int distance) {
            this.word = word;
            this.distance = distance;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file path to load the graph:");
        String filePath = scanner.nextLine();
        // Load graph from file (implementation not shown)
        Graph graph = new Graph();
        try {
            graph.loadGraphFromFile(filePath);
            System.out.println("Graph loaded successfully.");
        } catch (IOException e) {
            System.err.println("Error loading file: " + e.getMessage());
            System.exit(1);
        }

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Show Directed Graph");
            System.out.println("2. Query Bridge Words");
            System.out.println("3. Generate New Text");
            System.out.println("4. Calculate Shortest Path");
            System.out.println("5. Calculate PageRank");
            System.out.println("6. Random Walk");
            System.out.println("7. Exit");

            String input = scanner.nextLine().trim(); // 读取用户输入
            if (input.isEmpty()) {
                System.out.println("Invalid input. Please enter a number between 1 and 7.");
                continue;
            }

            try {
                int choice = Integer.parseInt(input); // 尝试将输入解析为整数
                switch (choice) {
                    case 1:
                        graph.showDirectedGraph();
                        break;
                    case 2:
                        System.out.println("Enter words:");
                        String word1 = scanner.nextLine();
                        String word2 = scanner.nextLine();
                        System.out.println(graph.queryBridgeWords(word1, word2));
                        break;
                    case 3:
                        System.out.println("Enter input text:");
                        String inputText = scanner.nextLine();
                        System.out.println(graph.generateNewText(inputText));
                        break;
                    case 4:
                        System.out.println("Enter words:");
                        word1 = scanner.nextLine();
                        word2 = scanner.nextLine();
                        System.out.println(graph.calcShortestPath(word1, word2));
                        break;
                    case 5:
                        System.out.println("Enter word:");
                        String word = scanner.nextLine();
                        System.out.println("PageRank of '" + word + "':" + graph.calPageRank(word));
                        break;
                    case 6:
                        System.out.println(graph.randomWalk());
                        break;
                    case 7:
                        System.out.println("Exiting...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 7.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    public void showDirectedGraph() {
        // Implementation to display the directed graph
        if (adjacencyList.isEmpty()) {
            System.out.println("The graph is empty.");
            return;
        }

        // 收集所有节点（字母排序）
        Set<String> nodes = new TreeSet<>(adjacencyList.keySet());
        adjacencyList.values().forEach(edges -> nodes.addAll(edges.keySet()));

        System.out.println("\nNodes in the graph:");
        nodes.forEach(node -> System.out.println("- " + node));

        System.out.println("\nEdges in the graph:");
        adjacencyList.forEach((source, edges) -> {
            edges.forEach((target, weight) -> {
                System.out.printf("%s -> %s (weight: %d)%n", source, target, weight);
            });
        });

        generateGraphImage("graph.png");
        System.out.println("Graph visualization saved to graph.png");
    }

    public String queryBridgeWords(String word1, String word2) {
        // 处理输入：过滤非字母字符、转换为小写
        // Implementation to query bridge words
        // 统一转换为小写处理
        String w1 = word1.toLowerCase();
        String w2 = word2.toLowerCase();

        // 检查单词是否存在
        if (!adjacencyList.containsKey(w1) || !adjacencyList.containsKey(w2)) {
            if (!adjacencyList.containsKey(w1) && !adjacencyList.containsKey(w2)) {
                return "No " + word1 + " and " + word2 + " in the graph!";
            } else if (!adjacencyList.containsKey(w1)) {
                return "No " + word1 + " in the graph!";
            } else {
                return "No " + word2 + " in the graph!";
            }
        }

        // 寻找桥接词
        List<String> bridgeWords = new ArrayList<>();
        Map<String, Integer> w1Edges = adjacencyList.get(w1);

        // 遍历所有w1的邻接节点
        for (String candidate : w1Edges.keySet()) {
            // 检查候选词是否指向w2
            if (adjacencyList.containsKey(candidate) &&
                    adjacencyList.get(candidate).containsKey(w2)) {
                bridgeWords.add(candidate);
            }
        }

        // 处理结果
        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        } else {
            return formatBridgeWordsOutput(bridgeWords, word1, word2);
        }

    }

    public String generateNewText(String inputText) {
        // Implementation to generate new text
        // 处理输入文本：过滤非字母字符、分割单词
        String processedInput = inputText.replaceAll("[^a-zA-Z ]", " ").trim();
        if (processedInput.isEmpty()) {
            return "";
        }

        String[] rawWords = processedInput.split("\\s+");
        List<String> rawWordList = new ArrayList<>(Arrays.asList(rawWords));
        List<String> lowerWords = new ArrayList<>();
        for (String word : rawWordList) {
            lowerWords.add(word.toLowerCase());
        }

        // 收集需要插入的位置和桥接词
        List<Insertion> insertions = new ArrayList<>();
        Random rand = new Random();

        // 遍历所有相邻词对
        for (int i = 0; i < lowerWords.size() - 1; i++) {
            String current = lowerWords.get(i);
            String next = lowerWords.get(i + 1);

            // 获取桥接词列表
            List<String> bridges = getBridgeWords(current, next);
            if (!bridges.isEmpty()) {
                // 随机选择桥接词
                String selected = bridges.get(rand.nextInt(bridges.size()));
                insertions.add(new Insertion(i + 1, selected));
            }
        }

        // 按逆序插入以避免索引错位
        Collections.sort(insertions, (a, b) -> b.position - a.position);
        for (Insertion ins : insertions) {
            rawWordList.add(ins.position, ins.word);
        }

        return String.join(" ", rawWordList);
    }

    public String calcShortestPath(String word1, String word2) {
        // Implementation to calculate shortest path
        String w1 = word1.toLowerCase().trim();
        String w2 = word2.toLowerCase().trim();

        // 处理单字查询
        boolean singleWordMode = w2.isEmpty();

        if (singleWordMode) {
            return handleSingleWord(w1);
        } else {
            return handleTwoWords(w1, w2);
        }
    }

    public Double calPageRank(String word) {
        // Implementation to calculate PageRank
        String targetWord = word.toLowerCase().trim();
        if (!adjacencyList.containsKey(targetWord)) {
            return -1.0; // 表示单词不存在
        }

        calculatePageRank(0.85, 100, 0.0001);
        return pageRank.getOrDefault(targetWord, -1.0);
    }

    public String randomWalk() {
        if (adjacencyList.isEmpty()) {
            return "Graph is empty! No walk possible.";
        }

        final int STEP_DELAY = 2000; // 每步延迟2秒
        final int POLL_INTERVAL = 100; // 每100毫秒检查停止

        // 初始化随机组件
        Random rand = new Random();
        List<String> nodeList = new ArrayList<>(adjacencyList.keySet());
        String currentNode = nodeList.get(rand.nextInt(nodeList.size()));

        // 数据记录结构
        List<String> path = new ArrayList<>(Collections.singletonList(currentNode));
        Set<String> visitedEdges = new HashSet<>();
        final boolean[] stopFlag = { false };

        // 输入监听线程
        Scanner globalScanner = new Scanner(System.in); // 使用全局Scanner
        Thread inputThread = new Thread(() -> {
            while (!stopFlag[0] && !Thread.currentThread().isInterrupted()) {
                if (globalScanner.hasNextLine()) {
                    String input = globalScanner.nextLine().trim();
                    if (input.equalsIgnoreCase("stop")) {
                        stopFlag[0] = true;
                        break;
                    }
                }
            }
        });
        inputThread.setDaemon(true);
        inputThread.start();

        // 主游走循环
        while (true) {
            // 显示当前路径
            System.out.println("\nCurrent path: " + String.join(" → ", path));
            System.out.print("Next step in " + (STEP_DELAY / 1000) + "s (输入'stop'终止)...");

            // 带中断检查的延迟
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < STEP_DELAY) {
                try {
                    Thread.sleep(POLL_INTERVAL);
                    if (stopFlag[0]) {
                        System.out.println("\n[用户终止]");
                        break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            if (stopFlag[0])
                break;

            Map<String, Integer> edges = adjacencyList.get(currentNode);
            if (edges == null || edges.isEmpty()) {
                System.out.println("[No outgoing edges]");
                break;
            }

            // 加权随机选择
            int totalWeight = edges.values().stream().mapToInt(Integer::intValue).sum();
            if (totalWeight == 0)
                break;
            int random = rand.nextInt(totalWeight);
            int accumulator = 0;
            String nextNode = null;

            for (Map.Entry<String, Integer> entry : edges.entrySet()) {
                accumulator += entry.getValue();
                if (random < accumulator) {
                    nextNode = entry.getKey();
                    break;
                }
            }

            // 检查边是否重复
            String edge = currentNode + "→" + nextNode;
            if (visitedEdges.contains(edge)) {
                System.out.println("[Repeated edge: " + edge + "]");
                break;
            }

            // 显示边选择动画
            System.out.print("\n选择边 " + currentNode + " → ");
            for (int i = 0; i < 3; i++) {
                System.out.print(".");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
            }
            System.out.println(" " + nextNode);

            // 更新状态
            visitedEdges.add(edge);
            path.add(nextNode);
            currentNode = nextNode;
        }

        // 终止输入线程
        stopFlag[0] = true;
        inputThread.interrupt();

        // 生成输出
        String result = String.join(" → ", path);

        // 写入文件
        try (PrintWriter writer = new PrintWriter("random_walk.txt")) {
            writer.println("Random Walk Path:");
            writer.println(result);
            writer.printf("\nTotal Steps: %d", path.size() - 1);
        } catch (FileNotFoundException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        return "Random walk path:\n" + result +
                "\n\nResult saved to random_walk.txt";
    }

    private void loadGraphFromFile(String filePath) throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String processedLine = line
                        .replaceAll("[^a-zA-Z]", " ")
                        .toLowerCase();

                String[] lineWords = processedLine.split("\\s+");
                for (String word : lineWords) {
                    if (!word.isEmpty()) {
                        words.add(word);
                    }
                }
            }
        }
        buildGraph(words);
    }

    private void buildGraph(List<String> words) {
        wordFrequency = new HashMap<>();
        outWeights = new HashMap<>();
        reverseAdjacencyList = new HashMap<>();

        // 统计词频
        words.forEach(word -> wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1));

        // 构建邻接表和反向邻接表
        for (int i = 0; i < words.size() - 1; i++) {
            String current = words.get(i);
            String next = words.get(i + 1);

            // 正向边
            adjacencyList.putIfAbsent(current, new HashMap<>());
            int count = adjacencyList.get(current).getOrDefault(next, 0) + 1;
            adjacencyList.get(current).put(next, count);

            // 反向边
            reverseAdjacencyList.putIfAbsent(next, new HashSet<>());
            reverseAdjacencyList.get(next).add(current);

            // 更新出边总权重
            outWeights.put(current,
                    outWeights.getOrDefault(current, 0.0) + count);
        }
    }

    private void generateGraphImage(String outputPath) {
        try {
            // 1. 生成DOT文件内容
            String dotContent = generateDotFileContent();

            // 2. 创建临时DOT文件
            File dotFile = new File("temp_graph.dot");
            try (PrintWriter writer = new PrintWriter(dotFile)) {
                writer.println(dotContent);
            }

            // 3. 调用Graphviz生成图像
            String[] cmd = {
                    "dot",
                    "-Tpng",
                    dotFile.getAbsolutePath(),
                    "-o",
                    outputPath
            };

            Process process = Runtime.getRuntime().exec(cmd);
            int exitCode = process.waitFor();

            // 4. 错误处理
            if (exitCode != 0) {
                BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()));
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    System.err.println("Graphviz Error: " + errorLine);
                }
                throw new RuntimeException("Graphviz execution failed");
            }

            // 5. 清理临时文件
            dotFile.delete();

        } catch (IOException | InterruptedException e) {
            System.err.println("Error generating graph image: " + e.getMessage());
        }
    }

    private String generateDotFileContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n");
        sb.append("    rankdir=LR;\n");
        sb.append("    node [shape=circle];\n");

        // 添加所有边
        adjacencyList.forEach((source, targets) -> {
            targets.forEach((target, weight) -> {
                sb.append(String.format("    \"%s\" -> \"%s\" [label=\"%d\"];\n",
                        source, target, weight));
            });
        });

        sb.append("}\n");
        return sb.toString();
    }

    private String formatBridgeWordsOutput(List<String> words, String w1, String w2) {
        StringBuilder sb = new StringBuilder();
        sb.append("The bridge word from ")
                .append(w1).append(" to ").append(w2).append(" is: ");

        if (words.size() == 1) {
            sb.append(words.get(0));
        } else {
            for (int i = 0; i < words.size() - 1; i++) {
                if (i == words.size() - 2) {
                    sb.append(words.get(i)).append(" and ");
                } else {
                    sb.append(words.get(i)).append(", ");
                }
            }
            sb.append(words.get(words.size() - 1));
        }
        sb.append(".");
        return sb.toString();
    }

    private List<String> getBridgeWords(String word1, String word2) {
        List<String> result = new ArrayList<>();

        // 检查节点存在性
        if (!adjacencyList.containsKey(word1) || !adjacencyList.containsKey(word2)) {
            return result;
        }

        // 遍历所有可能的桥接词
        adjacencyList.get(word1).keySet().forEach(candidate -> {
            if (adjacencyList.containsKey(candidate) &&
                    adjacencyList.get(candidate).containsKey(word2)) {
                result.add(candidate);
            }
        });

        return result;
    }

    private static class Insertion {
        int position;
        String word;

        public Insertion(int position, String word) {
            this.position = position;
            this.word = word;
        }
    }

    private String handleSingleWord(String word) {
        if (!adjacencyList.containsKey(word)) {
            return "No " + word + " in the graph!";
        }

        dijkstra(word);
        StringBuilder result = new StringBuilder();
        result.append("Shortest paths from ").append(word).append(":\n");

        adjacencyList.keySet().stream()
                .filter(node -> !node.equals(word))
                .forEach(node -> {
                    int dist = distances.get(node);
                    if (dist == Integer.MAX_VALUE) {
                        result.append(node).append(": unreachable\n");
                    } else {
                        List<List<String>> paths = getAllPaths(word, node);
                        if (!paths.isEmpty()) {
                            result.append(node)
                                    .append(" (").append(dist).append("): ")
                                    .append(String.join(" -> ", paths.get(0)))
                                    .append("\n");
                        }
                    }
                });

        return result.toString();
    }

    private String handleTwoWords(String w1, String w2) {
        // 验证节点存在性
        if (!adjacencyList.containsKey(w1)) {
            return "No " + w1 + " in the graph!";
        }
        if (!adjacencyList.containsKey(w2)) {
            return "No " + w2 + " in the graph!";
        }

        dijkstra(w1);
        int distance = distances.get(w2);

        if (distance == Integer.MAX_VALUE) {
            return w1 + " and " + w2 + " are not reachable!";
        }

        List<List<String>> allPaths = getAllPaths(w1, w2);
        StringBuilder result = new StringBuilder();
        result.append("Shortest path length: ").append(distance).append("\n");

        if (allPaths.size() == 1) {
            result.append("Path: ").append(String.join(" -> ", allPaths.get(0)));
            generatePathImage(allPaths.get(0), "shortest_path.png");
            result.append("\nVisualization saved to shortest_path.png");
        } else {
            result.append("Found ").append(allPaths.size()).append(" shortest paths:\n");
            for (int i = 0; i < allPaths.size(); i++) {
                String filename = "path_" + (i + 1) + ".png";
                result.append(i + 1).append(": ").append(String.join(" -> ", allPaths.get(i)))
                        .append(" [").append(filename).append("]\n");
                generatePathImage(allPaths.get(i), filename);
            }
        }

        return result.toString();
    }

    private void dijkstra(String start) {
        distances = new HashMap<>();
        predecessors = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));

        // 初始化数据结构
        adjacencyList.keySet().forEach(node -> {
            distances.putIfAbsent(node, Integer.MAX_VALUE);
            predecessors.putIfAbsent(node, new ArrayList<>());
        });

        // 确保所有邻接节点也被初始化
        adjacencyList.values().forEach(edges -> {
            edges.keySet().forEach(node -> {
                distances.putIfAbsent(node, Integer.MAX_VALUE);
                predecessors.putIfAbsent(node, new ArrayList<>());
            });
        });

        distances.put(start, 0);
        pq.add(new Node(start, 0));

        // 打印初始化后的 distances
        System.out.println("Initial distances: " + distances);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            String u = current.word;

            if (current.distance > distances.get(u))
                continue;

            adjacencyList.getOrDefault(u, Collections.emptyMap())
                    .forEach((v, weight) -> {
                        int newDist = distances.get(u) + weight;

                        if (newDist < distances.get(v)) {
                            distances.put(v, newDist);
                            predecessors.get(v).clear();
                            predecessors.get(v).add(u);
                            pq.add(new Node(v, newDist));
                        } else if (newDist == distances.get(v)) {
                            if (!predecessors.get(v).contains(u)) {
                                predecessors.get(v).add(u);
                            }
                        }
                    });
            // 打印每次更新后的 distances
            System.out.println("Updated distances: " + distances);
        }
    }

    private List<List<String>> getAllPaths(String start, String end) {
        List<List<String>> paths = new ArrayList<>();
        LinkedList<String> path = new LinkedList<>();
        path.add(end); // 从终点开始回溯
        backtrack(start, end, path, paths);
        return paths;
    }

    private void backtrack(String start, String current,
            LinkedList<String> path, List<List<String>> paths) {
        if (current.equals(start)) {
            // 直接添加路径，无需反转
            paths.add(new ArrayList<>(path));
            return;
        }

        for (String pred : predecessors.get(current)) {
            path.addFirst(pred); // 将前驱节点插入链表头部
            backtrack(start, pred, path, paths);
            path.removeFirst(); // 回溯
        }
    }

    private void generatePathImage(List<String> path, String filename) {
        try {
            String dotContent = generatePathDotContent(path);
            File dotFile = File.createTempFile("graph", ".dot");

            try (PrintWriter writer = new PrintWriter(dotFile)) {
                writer.println(dotContent);
            }

            String[] cmd = {
                    "dot", "-Tpng", dotFile.getAbsolutePath(),
                    "-o", filename
            };

            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            dotFile.delete();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error generating path visualization: " + e.getMessage());
        }
    }

    private String generatePathDotContent(List<String> path) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n")
                .append("    rankdir=LR;\n")
                .append("    node [shape=circle];\n");

        // 添加所有普通边
        adjacencyList.forEach((source, targets) -> {
            targets.forEach((target, weight) -> {
                String color = isPathEdge(source, target, path) ? "green" : "black";
                sb.append(String.format("    \"%s\" -> \"%s\" [label=\"%d\" color=\"%s\"];\n",
                        source, target, weight, color));
            });
        });

        sb.append("}\n");
        return sb.toString();
    }

    private void calculatePageRank(double damping, int maxIterations, double threshold) {
        // 初始化PageRank值
        pageRank = new HashMap<>();
        double totalFreq = wordFrequency.values().stream()
                .mapToInt(Integer::intValue).sum();

        // 基于词频的初始值分配
        adjacencyList.keySet().forEach(node -> pageRank.put(node, wordFrequency.get(node) / totalFreq));

        boolean converged;
        int iterations = 0;
        int totalNodes = adjacencyList.size();
        do {
            Map<String, Double> newRank = new HashMap<>();
            double danglingPR = 0.0;

            // 计算悬挂节点的PR总和
            for (String node : adjacencyList.keySet()) {
                if (adjacencyList.get(node).isEmpty()) {
                    danglingPR += pageRank.get(node);
                }
            }

            // 计算悬挂节点贡献（当节点数>1时）
            double danglingContribution = 0.0;
            if (totalNodes > 1) {
                danglingContribution = damping * danglingPR / (totalNodes - 1);
            }

            // 计算新的PR值
            for (String node : adjacencyList.keySet()) {
                double pr = (1 - damping) / totalNodes; // 随机跳转部分
                pr += danglingContribution;

                // 正常入链贡献
                for (String inbound : reverseAdjacencyList.getOrDefault(node, new HashSet<>())) {
                    double totalWeight = outWeights.getOrDefault(inbound, 0.0);
                    if (totalWeight > 0) {
                        double weight = adjacencyList.get(inbound).get(node);
                        pr += damping * pageRank.get(inbound) * (weight / totalWeight);
                    }
                }

                newRank.put(node, pr);
            }

            // 检查收敛
            converged = true;
            for (String node : adjacencyList.keySet()) {
                if (Math.abs(newRank.get(node) - pageRank.get(node)) > threshold) {
                    converged = false;
                    break;
                }
            }

            pageRank = newRank;
            iterations++;
        } while (!converged && iterations < maxIterations);
    }

}

// s2r4修改1