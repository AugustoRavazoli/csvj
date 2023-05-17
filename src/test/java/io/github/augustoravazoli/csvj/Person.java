package io.github.augustoravazoli.csvj;

@CsvSerializable
class Person {

  @CsvColumn
  private String name;

  @CsvColumn
  private int age;

  private double salary;

  Person(String name, int age, double salary) {
    this.name = name;
    this.age = age;
    this.salary = salary;
  }

  public String getName() {
    return name;
  }

  public int getAge() {
    return age;
  }

  public double getSalary() {
    return salary;
  }

}
