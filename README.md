

# This is backend test - Nguyen Xuan Loc

Foobar is a Python library for dealing with word pluralization.

## Installation

Using docker.

```bash
docker pull locnx1105/backend-test
```

## Usage
Variable :
1. email_template_path (json): your email template path.
2. customer_csv_path (csv): your customer csv path.
3. output_email_directory (directory) : output email folder path.
4. errors_csv_path (csv) : your error csv path.

```python
# run 'docker image'
docker run -v {email_template}:/email_template.json -v {customer_csv}:/customers.csv -v {output_email_directory}:/Output -v {errors_csv_path}:/errors.csv -ti backend-test

# How to Enter input 
Enter your email template : /email_template.json
Enter your customer file path : /customers.csv
Output email path : /Output
Output errors csv path : /errors.csv


```
## Result
Check {output_email_directory} and {errors_csv_path}.

## Contributing
Pull requests are welcome. This is backend test.

## License
[Loc](locnx1105@gmail.com)
