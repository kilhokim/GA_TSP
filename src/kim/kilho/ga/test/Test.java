package kim.kilho.ga.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;


/**
 * Test class, only for test-purpose
 */
public class Test {
  static final int MAXN = 600; // Maximum value of N
  static final int PSIZE = 50; // Size of the population
  static final int GENERATION = 1000000000; // The number of generations
  static int CurrentGen = 0;

  // The i-th location is (PX[i], PY[i]),
  // and the distance between i-th and j-th location is Dist[i][j]
  static double Dist[][] = new double [MAXN][MAXN];
  static double PX[] = new double [MAXN];
  static double PY[] = new double [MAXN];
  static int N;

  // Time limit for the test case, in milliseconds
  static long TimeLimit;

  // The i-th solution in the population is X[i],
  // and its fitness is Fitness[i]
  static int X[][] = new int [PSIZE][MAXN];
  static double Fitness[] = new double [PSIZE];
  static double SelectFitness[] = new double [PSIZE];

  // The child(offspring) and its fitness
  static int Child[] = new int [MAXN];
  static double ChildFitness;

  // The best(found) solution and its fitness
  static int Optimum[] = new int [MAXN];
  static double OptimumFitness = 1e100;

  // Timestamp at the beginning of the program execution
  static long BeginTime;

  // Random number generator
  static Random random;

  // Selection Parameter
  static double TournamentSP = 0.8;
  static int TR = 0;

  // CrossOver Parameter
  static int OX= 0;

  // Mutation Parameter
  static double MUPr = 0.2;
  static int IVM= 0;

  // Reads the test case from standard input
  static void Input(String fileName)
  {
    int i = 0;
    int j = 0;
    double time_limit = 0;
    BufferedReader br  = null;
    try{
      File file = new File(fileName);
      FileReader reader = new FileReader(file);
      br = new BufferedReader(reader);
      String line ;
      int  currentLine = 0;
      while( (line = br.readLine()) != null ){
        if(currentLine == 0){
          N = Integer.parseInt(line);
        }
        else if( currentLine == N+1){
          time_limit = Double.parseDouble(line);
          TimeLimit = (long)(time_limit * 1000);
        }
        else{
          String[] coordinate = line.split(" ");
          PX[i] = Double.parseDouble(coordinate[0]);
          PY[i] = Double.parseDouble(coordinate[1]);
          i++;
        }
        currentLine++;
      }
      br.close();
      reader.close();
    }catch(Exception e){
      e.printStackTrace();
    }
    for(i = 0; i < N; i++) for(j = 0; j < N; j++){
      double dx = PX[i] - PX[j];
      double dy = PY[i] - PY[j];
      Dist[i][j] = Math.sqrt(dx*dx + dy*dy);
    }
  }

  // Initializes the population and computes the fitness
  static void Initialize()
  {
    int i, j;
    for(i = 0; i < PSIZE; i++)
    {
      for(j = 0; j < N; j++) X[i][j] = j;
      for(j = 0; j < N; j++)
      {
        int index1 = random.nextInt(N);
        int index2 = random.nextInt(N);
        int temp = X[i][index1];
        X[i][index1] = X[i][index2];
        X[i][index2] = temp;
      }
      Fitness[i] = 0;
      OptimumFitness = 1e100;
      for(j = 0; j < N; j++)
      {
        Fitness[i] += Dist[X[i][j]][X[i][(j+1)%N]];
      }
    }
  }

  // Selection operator
  static int  Selection(int selectionMethod)
  {
    int selectedIndex = 0;
    switch(selectionMethod){
      case 0: selectedIndex = Tournament(); break;
    }
    return selectedIndex;
  }

  static int Tournament(){
    int selectedIndex = 0;
    int index1 = random.nextInt(PSIZE);
    int index2 = random.nextInt(PSIZE);
    int goodOne;
    int badOne;
    if(Fitness[index1] < Fitness[index2]){
      goodOne = index1;
      badOne = index2;
    }
    else{
      goodOne = index2;
      badOne = index1;
    }

    double r = Math.random();
    if(TournamentSP > r) selectedIndex = goodOne;
    else selectedIndex = badOne;
    return selectedIndex;
  }

  // Crossover operator
  static void Crossover(int p1, int p2, int crossoverMethod)
  {
    switch(crossoverMethod){
      case 0: OX(p1, p2); break;
    }
  }

  static void OX(int p1, int p2){
    int startSubTour= random.nextInt(N);
    int length = random.nextInt(N-startSubTour);
    int lastSubTour = startSubTour + length;
    LinkedList<Integer> subTour = new LinkedList<Integer>();
    LinkedList<Integer> remainTour = new LinkedList<Integer>();
    for(int i = 0; i<N ; i++){
      remainTour.add(X[p2][i]);
    }
    for(int i =startSubTour; i <= lastSubTour; i ++){
      subTour.add(X[p1][i]);
    }
    for( int i = 0; i <subTour.size() ; i++){
      int tempIndex = remainTour.indexOf(subTour.get(i));
      remainTour.remove(tempIndex);
    }
    for( int i = 0; i< subTour.size(); i++){
      remainTour.add(subTour.get(i));
    }
    for(int i = 0; i < N; i++){
      Child[i] = remainTour.get(i);
    }
  }

  // Mutation operator
  static void Mutation(int mutationMethod)
  {
    switch(mutationMethod){
      case 0: IVM(); break;
    }
  }

  static void IVM(){
    int startSubTour= random.nextInt(N);
    int length = random.nextInt(N-startSubTour);
    int lastSubTour = startSubTour + length;
    int index;
    LinkedList<Integer> remainTour = new LinkedList<Integer>();
    LinkedList<Integer> subTour = new LinkedList<Integer>();
    for( int i = 0; i <N ; i++){
      if(startSubTour <= i &&  i <= lastSubTour) subTour.add(Child[i]);
      else remainTour.add(Child[i]);
    }
    if( remainTour.size() == 0){
      for(int i = 0 ; i<N ; i++) {
        Child[i] = (int) subTour.get(N-1-i);
      }
    }
    else{
      index = random.nextInt(remainTour.size());
      for( int i = 0; i < N; i++){
        if( i <= index ) Child[i] =  (int) remainTour.get(i);
        else if(0<= i -index -1 && i -index -1 < subTour.size()){
          Child [i] = (int) subTour.get(index+ subTour.size() -i);
        }
        else{
          Child[i] = (int) remainTour.get(i - subTour.size());
        }
      }
    }
  }

  // Local Optimization
  static void TwoOpt(int [] route){
    int count = 0;
    int [] newRoute = new int[route.length];
    double bestDistance = 0;
    boolean repeat = true;
    while(repeat){
      repeat = false;
      out:	for(int i = 0; i< N-1; i ++){
        for(int k= i +1 ; k < N-1; k++){
          count++;
          double newDistance= 0;
          bestDistance= Dist[route[(i-1+N)%N]][route[i]] + Dist[route[k]][route[(k+1)%N]];
          newDistance =  Dist[route[(i-1+N)%N]][route[k]] + Dist[route[i]][route[(k+1)%N]];
          if( newDistance < bestDistance) {
            for( int j = 0; j <N ; j ++){
              int l = i+k;
              if(i <=j && j <=k) newRoute[j] = route[l-j];
              else newRoute[j] = route[j];
            }
            for(int m = 0; m < N; m++) route[m] = newRoute[m];
            repeat = true;
            break out;
          }
        }
      }
    }
    System.out.println(count);
  }

  // Renews Optimum and OptimumFitness, if a new optimum is found
  static void Evaluation( boolean restart,boolean log)
  {
    int i;
    ChildFitness = 0;
    for(i = 0; i < N; i++)
    {
      ChildFitness += Dist[Child[i]][Child[(i+1)%N]];
    }
    if(ChildFitness < OptimumFitness){
      OptimumFitness = ChildFitness;
      if(log) System.out.println(CurrentGen +"\t" +OptimumFitness+"\t");
      for(i = 0; i < N; i++) Optimum[i] = Child[i];
    }
  }

  static void Replacement(int p1, int p2)
  {
    int i;
    int index;
    boolean replace = true;
    for(i = 0; i < PSIZE; i++){
      if(Fitness[i] == ChildFitness){
        replace = false;
        break;
      }
    }
    if(replace == true){
      if( Fitness[p1] > Fitness[p2]) index = p1;
      else index = p2;
      if(Fitness[index] > ChildFitness){
        for(i = 0; i < N; i++)
        {
          X[index][i] = Child[i];
        }
        Fitness[index] = ChildFitness;
      }
      else{
        double worstFitness = 0;
        int worstIndex = -1;
        for(i = 0; i < PSIZE; i++){
          if(Fitness[i] > worstFitness){
            worstFitness = Fitness[i];
            worstIndex = i;}
        }
        for(i = 0; i < N; i++)
        {
          X[worstIndex][i] = Child[i];
        }
        Fitness[worstIndex] = ChildFitness;
      }
    }
  }

  // The main procedure of the genetic algorithm
  static void GA(int selectionMethod, int crossoverMethod, int mutationMethod,  boolean restart, boolean log, int perGen)
  {
    int generation;
    Initialize();
    for( int i = 0 ; i < PSIZE ; i++){
      TwoOpt(X[i]);
    }
    int add = perGen;
    for(generation = 0; generation < GENERATION; generation++){
      CurrentGen = generation;
      if(System.currentTimeMillis() - BeginTime >= (TimeLimit-1500)) return;
      int p1 = Selection(selectionMethod);
      int p2 = Selection(selectionMethod);;
      if(Math.random() > 0.3) Crossover(p1, p2, crossoverMethod);
      else Crossover(p1, p2, OX);
      if(Math.random() < MUPr) Mutation(mutationMethod);
      TwoOpt(Child);
      Evaluation(restart, log);
      Replacement(p1, p2);

      if(perGen > 0){
        if(CurrentGen == perGen){
          double sum = 0;
          double best = Fitness[0];
          for(int i = 0 ; i < PSIZE; i++){
            sum += Fitness[i];
            if(Fitness[i] < best){
              best = Fitness[i];
            }
          }
          System.out.println(perGen+"\t"+ sum/PSIZE +"\t" + best );
          perGen += add;
        }
      }

    }
  }
  static void Output()
  {
    int i;
//		System.out.print(CurrentGen+"\t"+OptimumFitness+"\t");
    for(i = 0; i < N; i++)
    {
      if(i > 0) System.out.print(" ");
      System.out.print(Optimum[i]+1);
    }
    System.out.println();
  }

  public static void main(String args[]) throws Exception
  {
//		runGA("cycle.in.50", true, false, 0, 20);
//		runGA("cycle.in.100", true, false, 0, 20);
//		runGA("cycle.in.200", true, true, 0, 0);
		runGA("data/cycle.in.318", false, false, 0, 0);
    // runGA("cycle.in", false, false, 0, 0);
  }

  static void runGA(String fileName, boolean restart, boolean log, int perGen, int repeat){
    Input(fileName);
    if(repeat > 0){
      ArrayList<Double> ResultSet = new ArrayList<Double>();
      for( int i = 0 ;  i < repeat ; i++){
        BeginTime = System.currentTimeMillis();
        random = new Random();
        int selectionMethod = TR;
        int crossoverMethod= OX;
        int mutationMethod = IVM;
        GA(selectionMethod, crossoverMethod,mutationMethod, restart, log, perGen);
        Output();
        ResultSet.add(OptimumFitness);
      }
      Collections.sort(ResultSet);
      double temp = 0;
      for(int k = 0 ; k <  ResultSet.size(); k++){
        temp += ResultSet.get(k);
      }
      double best = ResultSet.get(0);
      double average = temp / (ResultSet.size());
      double sum = 0.0;
      double sd = 0.0;
      double diff;
      for( int k = 0; k <ResultSet.size();k ++){
        diff = ResultSet.get(k) - average;
        sum += diff*diff;
      }
      sd = Math.sqrt(sum / ResultSet.size());
      ResultSet.clear();
      System.out.println(best +"\t"+average+"\t"+sd );
    }
    else{
      BeginTime = System.currentTimeMillis();
      random = new Random();
      int selectionMethod = TR;
      int crossoverMethod= OX;
      int mutationMethod = IVM;
      GA(selectionMethod, crossoverMethod,mutationMethod,restart, log, perGen);
      Output();
    }
  }
}
